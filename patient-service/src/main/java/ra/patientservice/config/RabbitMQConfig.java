package ra.patientservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String USER_DELETE_QUEUE = "user_delete_queue";
    public static final String USER_EXCHANGE =  "user_exchange";
    public static final String USER_DELETE_ROUTING_KEY = "user.delete";
    @Bean
    public Queue userDeleteQueue() {
        return new Queue(USER_DELETE_QUEUE, true);
    }
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }
    @Bean
    public Binding userDeleteBinding(Queue userDeleteQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userDeleteQueue).to(userExchange).with(USER_DELETE_ROUTING_KEY);
    }
}
