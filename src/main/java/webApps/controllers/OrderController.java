package webApps.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import webApps.Application;
import webApps.domain.Order;
import webApps.domain.OrderRepository;
import webApps.domain.SystemDsc;
import webApps.domain.SystemRepository;
import webApps.excel.ExcelController;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public SystemRepository systemRepository;

    private static String NAME = "fileTemp";
    private static String fileInfo,editDataConsistency;

        /**
         *  search for corresponding name in system datatable
         * @return long - system ID
         */
        private long findSystemIdByName(String name) {
            SystemDsc systemDsc = systemRepository.findByName(name);
            return systemDsc.getId();
        }

        private boolean checkIfRightFormat(String iD, String fromDate, String toDate, String amount, String authPerc, String active) {
            String result= "";

            //checks iD format
            try {
                Long.valueOf(iD);
            }catch(NumberFormatException e) {
                result+=" iD val:["+iD+"] |";
            }

            //checks fromDate fromat
            try {
                Order.format.parse(fromDate);
            } catch (ParseException e) {
                result+=" from_date val:["+fromDate+"] |";
            }

            //checks toDate format
            try {
                Order.format.parse(toDate);
            } catch (ParseException e) {
                result+=" to_date val:["+toDate+"] |";
            }

            //checks amount format
            try {
                Float.valueOf(amount);
            } catch (NumberFormatException e) {
                result+= "amount val:["+amount+"] |";
            }

            //checks authorization percent
            try {
                Float.valueOf(authPerc);
            } catch (NumberFormatException e) {
                result+= "authorization % val:["+authPerc+"] |";
            }

            //checks active format
            try {
                Boolean.valueOf(active);
            } catch (NumberFormatException e) {
                result+= "active val:["+active+"] |";
            }

            if (result.equals("")) {
                editDataConsistency = "Values check OK";
                return true;
            }
            editDataConsistency="Incorect values in: "+result;
            return false;
        }
        private boolean checkIfRightFormatExcelData(String data[][]){

            //check if excell header is correct
            if(!(data[0][0].equals("system")&&
               data[0][1].equals("request") &&
               data[0][2].equals("order_number") &&
               data[0][3].equals("from_date")&&
               data[0][4].equals("to_date") &&
               data[0][5].equals("amount") &&
               data[0][6].equals("amount_type") &&
               data[0][7].equals("amount_period") &&
               data[0][8].equals("authorization_percent") &&
               data[0][9].equals("active"))) {
                fileInfo="Wrong excel header";
                return false;
            }
            //check if values are ok
            for (int i = 1; i <data.length ; i++) {
                //sets id=1, because excel file doesnt consist iD key
                if(!checkIfRightFormat("1",data[i][3],data[i][4],data[i][5],data[i][8],data[i][9]))
                    return false;
                }
                    return true;
        }

        /**
         *  uploads file, saves list of objects to DB, deletes file
         */
        @RequestMapping(method = RequestMethod.POST, value = "/uploadFile")
        public String handleFileUpload(@RequestParam("file") MultipartFile file) {
            String extension=".";
            String type=null;

            String uploadedFileName = file.getOriginalFilename();
            if (uploadedFileName.length()>5)
              extension= uploadedFileName.substring(uploadedFileName.length() - 4);

            if (extension.contains("xlsx"))
                type="xlsx";
            else if (extension.contains("xls"))
                type="xls";

            if (!file.isEmpty()) {
                try {
                    //                UPLOADING FILES
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(new File(Application.ROOT + "/" + NAME)));
                    FileCopyUtils.copy(file.getInputStream(), stream);
                    stream.close();
                    System.out.println("You successfully uploaded " + NAME + "!");

                    //        SAVE DATA TO DATABASE AND DELETE temporary file
                    String[][] dataStringTable=null;
                    Path filePath = Paths.get(Application.ROOT+"/"+NAME);
                    //        retrieve data as String[][]
                    try {
                        if(type.equals("xlsx")) {
                            dataStringTable = ExcelController.readXLSXFile(filePath.toString());
                            Files.delete(filePath);
                        }
                        else if(type.equals("xls")) {
                            dataStringTable = ExcelController.readXLSFile(filePath.toString());
                            Files.delete(filePath);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //save data if checked data ara valid
                    if (checkIfRightFormatExcelData(dataStringTable)) {
                        //retrieve list of Order objects
                        List<Order> listOfOrders = Order.retrieveListOfOrders(dataStringTable);
                        //add corresponding systemID
                        for (Order order : listOfOrders) {
                            order.setSystem_id(findSystemIdByName(order.getSystem()));
                        }
                        orderRepository.save(listOfOrders);
                        fileInfo = "File " + uploadedFileName + " uploaded";
                    }
                }
                catch (Exception e) {
                    fileInfo = "You failed to upload " + uploadedFileName + " => " + e.getMessage();
                }
            }
            else {
                fileInfo= "You failed to upload " + uploadedFileName + " because the file was empty";
            }
                return "redirect:/orders";
        }

        /**
         *  Initial page. Passes data from DB and provided file passing information
         */
        @RequestMapping(value = "", method = RequestMethod.GET)
        public String listOrders(Model model) {
            model.addAttribute("orders", orderRepository.findAll());
            //pass file Info
            model.addAttribute("fileInfo",fileInfo);
            //pass edit data consistency
            model.addAttribute("editDataConsistency",editDataConsistency);
            return "orders/list";
        }

        /**
         *  data deletion
         */
        @RequestMapping(value="/{id}/delete", method = RequestMethod.GET)
        public ModelAndView delete(@PathVariable long id) {
            orderRepository.delete(id);
            return new ModelAndView("redirect:/orders");
        }

        /**
         *  data deletion
         */
        @RequestMapping(value="/saveData",method = RequestMethod.POST)
        public ModelAndView create (@RequestParam("idEdit") String iD,
                                    @RequestParam("systemEdit") String system,
                                    @RequestParam("requestEdit") String request,
                                    @RequestParam("orderNumberEdit") String orderNumber,
                                    @RequestParam("fromDateEdit") String fromDate,
                                    @RequestParam("toDateEdit") String toDate,
                                    @RequestParam("amountEdit") String amount,
                                    @RequestParam("amountTypeEdit") String amountType,
                                    @RequestParam("amountPeriodEdit") String amountPeriod,
                                    @RequestParam("authorizationEdit") String authorization,
                                    @RequestParam("activeEdit") String active
                                    ){

           //saves the data into DB if date are correct. Doesnt check String value types
           if ( checkIfRightFormat(iD,fromDate,toDate,amount,authorization,active)) {

            Long  idL = Long.valueOf(iD);
            Order order=new Order();
            if (orderRepository.exists(idL))
                order = orderRepository.findOne(idL);
            order.setSystem(system);
            order.setRequest(request);
            order.setOrder_number(orderNumber);
            order.setFrom_date(fromDate);
            order.setTo_date(toDate);
            order.setAmount(amount);
            order.setAmount_type(amountType);
            order.setAmount_period(amountPeriod);
            order.setAuthorization_percent(authorization);
            order.setActive(active);

            //  find corresponding system id and set in system_contract table
            order.setSystem_id(findSystemIdByName(order.getSystem()));
            orderRepository.save(order);
           }
            return new ModelAndView("redirect:/orders");
        }
}