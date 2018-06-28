package com.amazon.asksdk.cqevida.Handlers.VidaHandlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.asksdk.cqevida.Utils.tell;

/**
 * Created by owenryan on 06/04/2018.
 */

//NOT TO BE CONFUSED WITH LaunchRequestHandler, which handles the built in launch skill intent. T
// his handler handles the "launch" intent created as debug and testing intent and is in no way related to the
// actual built in intent that triggers when a skill is first launched.
public class getLaunchResponseHandler implements RequestHandler{
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches("launch"))
        {
            return true;
        }
        return false;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        String speechText = "Test Successful: Launch!";
        return tell(handlerInput, speechText);
    }
}
