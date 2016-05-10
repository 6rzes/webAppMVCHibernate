package webApps.controllers;

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
import java.util.List;


@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public SystemRepository systemRepository;

    private static String NAME = "fileTemp";
    private static String fileInfo;


        /**
         *  search for corresponding name in system datatable
         * @return long - system ID
         */
        private long findSystemIdByName(String name) {
            SystemDsc systemDsc = systemRepository.findByName(name);
            return systemDsc.getId();
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

                    //retrieve list of Order objects
                    List<Order> listOfOrders = Order.retrieveListOfOrders(dataStringTable);
                    //add coresponding systemID
                    for(Order order: listOfOrders) {
                        order.setSystem_id(findSystemIdByName(order.getSystem()));
                    }
                    orderRepository.save(listOfOrders);
                    fileInfo= "File " + uploadedFileName + " uploaded";
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
         *  Initial page. Passes data from DB and provied file passing information
         */
        @RequestMapping(value = "", method = RequestMethod.GET)
        public String listOrders(Model model) {
            model.addAttribute("orders", orderRepository.findAll());
            //pass file Info
            model.addAttribute("fileInfo",fileInfo);
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

            //// TODO: 09.05.2016 check if every data in fields are correct (String, number etc.)

            Long idL=1L;
            try {
                idL = Long.valueOf(iD);
            }
            catch(NumberFormatException e) {
                fileInfo="Wrong format, id have to by a type Long number";
            }
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

        //  find corresponding system id and set in in system_contract table
            order.setSystem_id(findSystemIdByName(order.getSystem()));
            orderRepository.save(order);

            return new ModelAndView("redirect:/orders");
        }
}