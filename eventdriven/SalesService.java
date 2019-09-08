/** 
kamel run --name=sales-service -d camel-swagger-java -d camel-jackson -d camel-undertow -d mvn:org.apache.activemq:activemq-camel:5.15.9 -d mvn:org.apache.activemq:activemq-client:5.15.9 SalesService.java
kamel run --name=sales-service -d camel-swagger-java -d camel-jackson -d camel-undertow -d camel-ahc-ws -d mvn:org.apache.activemq:activemq-camel:5.15.9 -d mvn:org.apache.activemq:activemq-client:5.15.9 SalesService.java --dev
kamel run --name=sales-service -d camel-jackson -d camel-ahc-ws -d camel-amqp SalesService.java
*/
import java.util.HashMap;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.component.amqp.AMQPConnectionDetails;
import org.apache.camel.component.jackson.JacksonDataFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SalesService extends RouteBuilder {

    String BROKER_URL = "amqp://messaging-7rm3r5j1m3.workshop-operators.svc"+":5672"+"?amqp.saslMechanisms=PLAIN";
    String USERNAME = "user";
    String PWD = "enmasse";
    
    
    @Override
    public void configure() throws Exception {
        

        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        jacksonDataFormat.setUnmarshalType(Map.class);
        JacksonDataFormat salesDataFormat = new JacksonDataFormat();
        salesDataFormat.setUnmarshalType(SalesNotification.class);


        from("")
            .unmarshal(jacksonDataFormat)
            .bean(SalesNotification.class, "getSalesNotification(${body['orderId']},${body['price']} )")
            .marshal(salesDataFormat)
            .convertBodyTo(String.class)
            .log("Sales Notified ${body}")
            .to("")
            ;
        
    }


    
    private static class SalesNotification {
        private Integer orderId;
        private Integer price;
        private String department;
        private Date datetime;

        public static SalesNotification getSalesNotification(Integer orderId, Integer price ){
            SalesNotification salesNotification  = new SalesNotification();
            salesNotification.setOrderId(orderId);
            salesNotification.setPrice(price);
            salesNotification.setDepartment("sales");
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            salesNotification.setDatetime(new Date(System.currentTimeMillis()));
            return salesNotification;
        }

        public void setOrderId(Integer orderId){
            this.orderId=orderId;
        }
        public Integer getOrderId(){
            return this.orderId;
        }
        public void setPrice(Integer price){
            this.price=price;
        }
        public Integer getPrice(){
            return this.price;
        }
        public String getDepartment() {
            return department;
        }
        public void setDepartment(String department) {
            this.department = department;
        }
        public Date getDatetime() {
            return datetime;
        }
    
        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }
    }
    
}