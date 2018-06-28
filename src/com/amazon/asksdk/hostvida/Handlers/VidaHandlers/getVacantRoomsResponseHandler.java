package com.amazon.asksdk.hostvida.Handlers.VidaHandlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.asksdk.hostvida.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static com.amazon.asksdk.hostvida.Constants.slot_guestName;
import static com.amazon.asksdk.hostvida.Constants.slot_startDate;
import static com.amazon.asksdk.hostvida.Constants.slot_time;
import static com.amazon.asksdk.hostvida.Utils.delegate;
import static com.amazon.asksdk.hostvida.Utils.tell;

/**
 * Created by owenryan on 06/04/2018.
 */

//NOT TO BE CONFUSED WITH LaunchRequestHandler, which handles the built in launch skill intent. T
// his handler handles the "launch" intent created as debug and testing intent and is in no way related to the
// actual built in intent that triggers when a skill is first launched.
public class getVacantRoomsResponseHandler implements RequestHandler{
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches("getVacantRooms"))
        {
            return true;
        }
        return false;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();

        String time = "";
        try {
            time = formatTime(intent.getSlots().get(slot_time).getValue());
        }
        catch (NullPointerException e)
        {
            return delegate(handlerInput);
        }

        int numberOfVacantRooms = ((int) Integer.parseInt(time.charAt(1) + "" + time.charAt(0))) + 9;
        int numberOfReadyRooms = numberOfVacantRooms  -  (((time.charAt(1) - 48) * 2)); //-48 because char '0' equals int 48
        if(numberOfReadyRooms < 0)
        {
            numberOfReadyRooms = numberOfReadyRooms * -1;
        }
        int numberOfPreviousGuests = ((int) Integer.parseInt(time.charAt(3) + "" + time.charAt(4))) + 14;
        int numberOfClosedRooms = (int) (((int) Integer.parseInt(time.charAt(1) + "")) / 2);



        String speechText = numberOfReadyRooms + " of the " + numberOfVacantRooms + " currently vacant rooms are ready for new guests, " + numberOfPreviousGuests + " guests are staying over from last night and " + numberOfClosedRooms + " rooms are closed for maintenance.";
        return tell(handlerInput, speechText);
    }

    public String formatTime(String inTime)
    {
        switch (inTime)
        {
            case "MO": return "09:00";
            case "AF": return "12:00";
            case "EV": return "05:00";
            case "NI": return "09:00";
            default: return inTime;
        }
    }
}
