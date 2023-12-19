package com.nexus.customerimport.Service;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.AddressDraft;
import com.commercetools.api.models.common.AddressDraftBuilder;
import com.commercetools.api.models.customer.CustomerDraft;
import com.commercetools.api.models.customer.CustomerDraftBuilder;
import com.commercetools.sync.commons.exceptions.SyncException;
import com.commercetools.sync.customers.CustomerSync;
import com.commercetools.sync.customers.CustomerSyncOptions;
import com.commercetools.sync.customers.CustomerSyncOptionsBuilder;
import com.commercetools.sync.customers.helpers.CustomerSyncStatistics;
import com.nexus.customerimport.Client;



@Service
public class CSVService {

    String firstName;
    String lastName;
    String companyName;
    String email;
    String address;
    String city;
    String state;
    String county;
    String zip;
    String phone1;
    String phone2;
    String web;
    String cust_key;
    String password;
    String add_key;
    String combinedAddress;


    private boolean isCSVFile(Path filePath) {
        return filePath.getFileName().toString().equalsIgnoreCase("customer.csv");
    }

    private void moveFile(String sourcePath, String destinationPath) {
        try {
            Thread.sleep(1000);

            Files.move(Paths.get(sourcePath), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void startHotFolder(String folderPath) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path parentPath = Paths.get(folderPath);
            Path processPath = parentPath.resolve("process");
            Path successPath = parentPath.resolve("success");
            Path failurePath = parentPath.resolve("failure");

            Files.createDirectories(processPath);
            Files.createDirectories(successPath);
            Files.createDirectories(failurePath);


            parentPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE);


            CompletableFuture.runAsync(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                Path filePath = (Path) event.context();
                                String absolutePath = parentPath.resolve(filePath).toString();
                                if (isCSVFile(filePath)) {
                                    String processFolderPath = processPath.resolve(filePath).toString();
                                    moveFile(absolutePath, processFolderPath);
                                    try {
                                        processCSVFiles(processFolderPath);
                                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                                        moveFile(processFolderPath, successPath.resolve("customer_" + timeStamp + ".csv").toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        // Move to failure folder if processing fails
                                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                                        moveFile(processFolderPath, failurePath.resolve("customer_" + timeStamp + ".csv").toString());
                                    }
                                }
                            }
//                            else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                                // Handle file deletion if needed
//                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processCSVFiles(String filePath) {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withHeader());

//            BufferedReader br = new BufferedReader(new FileReader(filePath));
//            CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withHeader());

            List<CustomerDraft> customerDraftsList = new ArrayList<>();
            CustomerDraft customerDraft = null;
            Client client = new Client();

            ProjectApiRoot apiRoot = client.createApiClient();
            final Logger logger = (Logger) LoggerFactory.getLogger(CustomerSync.class);

            final CustomerSyncOptions customerSyncOptions =
                    CustomerSyncOptionsBuilder.of(apiRoot).batchSize(30).errorCallback((syncException, draft, customer, updateActions) ->
                            logger.error(String.valueOf(new SyncException("My customized message")), syncException)).build();

            final CustomerSync customerSync = new CustomerSync(customerSyncOptions);

            CustomerSyncStatistics customerSyncStatistics;

            for (CSVRecord csvRecord : csvParser) {

                Properties prop = readPropertiesFile("C:\\Users\\SanjayB(TADigital)\\Projects\\CTMS\\commercetools-microservices\\src\\main\\resources\\application.properties");

                firstName = csvRecord.get("first_name");
                lastName = csvRecord.get("last_name");
                companyName = csvRecord.get("company_name");
                email = csvRecord.get("email");
                address = csvRecord.get("address");
                city = csvRecord.get("city");
                state = csvRecord.get("state");
                county = csvRecord.get("county");
                zip = csvRecord.get("zip");
                phone1 = csvRecord.get("phone1");
                phone2 = csvRecord.get("phone2");
                web = csvRecord.get("web");

                combinedAddress = address + "," + county;

                cust_key = StringUtils.replace(email, "@", "");
                cust_key = StringUtils.replace(cust_key, ".", "");

                UUID uniquekey = UUID.randomUUID();
                password = uniquekey.toString();
                add_key = uniquekey.toString();

                final AddressDraft Address = AddressDraftBuilder.of()
                        .additionalAddressInfo(combinedAddress)
                        .country("US")
                        .city(city)
                        .state(state)
                        .pOBox(zip)
                        .phone(phone1)
                        .mobile(phone2)
                        .key(add_key)
                        .build();

                customerDraft = CustomerDraftBuilder.of()
                        .email(email)
                        .password(password)
                        .key(cust_key)
                        .addresses(Address)
                        .firstName(firstName)
                        .lastName(lastName)
                        .companyName(companyName)
                        .build();

                customerDraftsList.add(customerDraft);
            }

            customerSyncStatistics = customerSync.sync(customerDraftsList).toCompletableFuture().join();

            // Handle or log the statistics as needed
            System.out.println(customerSyncStatistics);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return prop;
    }

}
