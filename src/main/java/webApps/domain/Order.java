package webApps.domain;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name="system_contract")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long system_id;
    private double amount, authorization_percent;
    private Boolean active;
    private String amount_period, amount_type, order_number, request,system;
    private Date from_date, to_date;
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSystem_id() {
        return system_id;
    }

    public void setSystem_id(long system_id) {
        this.system_id = system_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = Double.valueOf(amount);
    }

    public double getAuthorization_percent() {
        return authorization_percent;
    }

    public void setAuthorization_percent(String authorization_percent) {
        this.authorization_percent = Double.valueOf(authorization_percent);
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = Boolean.valueOf(active);
    }

    public String getAmount_period() {
        return amount_period;
    }

    public void setAmount_period(String amount_period) {
        this.amount_period = amount_period;
    }

    public String getAmount_type() {
        return amount_type;
    }

    public void setAmount_type(String amount_type) {
        this.amount_type = amount_type;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getFrom_date() {
        if (from_date!=null)
            return from_date.toString().substring(0,10);
        else
            return "";
    }

    public void setFrom_date(String from_date) {

        Date date = new Date();
        if(from_date.length()>0) {

            try {
                date = format.parse(from_date);
                this.from_date = date;
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("wrong date format: " + e);
            }
        }
        this.from_date=date;
    }

    public String getTo_date() {
        if (to_date!=null)
            return to_date.toString().substring(0,10);
        else
            return "";
    }

    public void setTo_date(String to_date) {
        Date date = new Date();
        if(to_date.length()>0) {

            try {
                date = format.parse(to_date);
                this.to_date = date;
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("wrong date format: " + e);
            }
        }
        this.to_date = date;
    }

    public static List retrieveListOfOrders (String[][] excelData) {
        List listOfOrders=new ArrayList();
        Order order;
        int noOfRows= excelData.length;

        for (int i = 1; i <  noOfRows; i++) {
            order=new Order();
            order.setSystem(excelData[i][0]);
            order.setRequest(excelData[i][1]);
            order.setOrder_number(excelData[i][2]);
            order.setFrom_date(excelData[i][3]);
            order.setTo_date(excelData[i][4]);
            order.setAmount(excelData[i][5]);
            order.setAmount_type(excelData[i][6]);
            order.setAmount_period(excelData[i][7]);
            order.setAuthorization_percent(excelData[i][8]);
            order.setActive(excelData[i][9]);
            listOfOrders.add(order);
        }
        return listOfOrders;
    }
}
