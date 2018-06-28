package com.amazon.asksdk.hostvida.Handlers.VidaHandlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.asksdk.hostvida.Constants.slot_guestName;
import static com.amazon.asksdk.hostvida.Constants.slot_startDate;
import static com.amazon.asksdk.hostvida.Utils.delegate;
import static com.amazon.asksdk.hostvida.Utils.tell;

/**
 * Created by owenryan on 06/04/2018.
 */

//NOT TO BE CONFUSED WITH LaunchRequestHandler, which handles the built in launch skill intent. T
// his handler handles the "launch" intent created as debug and testing intent and is in no way related to the
// actual built in intent that triggers when a skill is first launched.
public class getDoormanWelcomeResponseHandler implements RequestHandler{
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches("getDoormanWelcomeDetials"))
        {
            return true;
        }
        return false;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();

        String guestName = "";
        try {
            guestName = intent.getSlots().get(slot_guestName).getValue();
        }
        catch (NullPointerException e)
        {
            return delegate(handlerInput);
        }


        if(guestName.matches(""))
        {
            return tell(handlerInput, "I'm sorry but I didn't hear any name there.");
        }

        int roomNumber = (int) guestName.charAt(0);
        String specialRequest = "";
        switch (guestName.length() % 5)
        {
            case 0: specialRequest = "with feather pillows as requested for two nights. Golf is booked for 18:00 and dinner for two at 21:00."; break;
            case 1: specialRequest = "with memory foam pillows as requested for four nights and dinner is booked for two at 21:00."; break;
            case 2: specialRequest = "for 1 night and dinner is booked for 12 at 18:30."; break;
            case 3: specialRequest = "for two nights. And a private dining room is booked for 21:00."; break;
            case 4: specialRequest = "with feather pillows and an infant's cot as requested for eight nights."; break;
            default: specialRequest = "for 1 night.";
        }

        String speechText = guestName + " is assigned room " + roomNumber + " " + specialRequest;
        return tell(handlerInput, speechText);
    }
}
