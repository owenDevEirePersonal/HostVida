package com.amazon.asksdk.hostvida.Handlers.general;

/**
 * Created by owenryan on 04/04/2018.
 */
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;

import java.util.Optional;

public class LaunchRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(Predicates.requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = "Who are we welcoming?";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Who are we welcoming?", speechText)
                .withReprompt(speechText)
                .build();
    }

}