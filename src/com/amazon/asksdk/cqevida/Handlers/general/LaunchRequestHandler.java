package com.amazon.asksdk.cqevida.Handlers.general;

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
        String speechText = "Welcome to the CIP Vida skill, what can I do for you?";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Welcome to the CIP Vida Skill", speechText)
                .withReprompt(speechText)
                .build();
    }

}