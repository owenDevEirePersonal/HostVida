package com.amazon.asksdk.hostvida.Handlers.general;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;

import java.util.Optional;

/**
 * Created by owenryan on 04/04/2018.
 */
public class allExceptionsHandler implements ExceptionHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput, Throwable throwable) {
        return true;
    }

    @Override
    public Optional<Response> handle(HandlerInput input, Throwable throwable) {
        return input.getResponseBuilder()
                .withSpeech("I'm sorry, An error occured in the skill: " + throwable.toString())
                .build();
    }
}
