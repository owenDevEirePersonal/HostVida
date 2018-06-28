package com.amazon.asksdk.hostvida.Handlers.genericVidaHandlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;
import com.amazon.asksdk.hostvida.Utils;
import com.amazonaws.util.json.JSONObject;

import java.net.URLEncoder;
import java.util.Optional;

import static com.amazon.asksdk.hostvida.Constants.*;
import static com.amazon.asksdk.hostvida.Utils.*;
import static com.amazon.asksdk.hostvida.Utils.tell;

/**
 * Created by owenryan on 06/04/2018.
 */
public class getGenericFunctionResponseHandler implements RequestHandler{
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches(request_GenericRequest))
        {
            return true;
        }
        return false;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        String startDate = intent.getSlots().get(slot_startDate).getValue();
        String endDate = intent.getSlots().get(slot_endDate).getValue();

        String dateName = "createdDate";
        String auth = "CitAuth";
        String primaryContentType = getSlotID(intent.getSlots().get(slot_PrimaryContentType));
        String conditions = "";
        String resultInstructions = "count";

        Optional<Response> errorResponse = handleInputsChecking(new String[]{startDate, endDate, primaryContentType}, handlerInput);
        if(errorResponse != null)
        {
            return errorResponse;
        }
        else
        {
            try {
                startDate = formatDate(startDate);
                endDate = formatDate(endDate);

                //[Json formatting]
                JSONObject jsonInput = new JSONObject();
                jsonInput.put(auth_input_AuthType, auth);
                jsonInput.put(input_generic_dateName, dateName);
                jsonInput.put(input_generic_startDate, startDate);
                jsonInput.put(input_generic_endDate, endDate);
                jsonInput.put(input_generic_primaryContentType, formatPrimaryContentType(primaryContentType));
                jsonInput.put(input_generic_Conditions, conditions);
                jsonInput.put(input_generic_result, resultInstructions);

                JSONObject jsonRequirements = new JSONObject();
                jsonInput.put(input_generic_Requirements, jsonRequirements);

                JSONObject jsonCustom = new JSONObject();
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                //Note if this method returns a fileNotFound Exception it is possible that conditions or requirements make reference to a class that does not exist in the dari database.
                //[Servlet Output Parsing]
                if(Utils.retrieveServletErrors(handlerInput, jsonOutputString) == null)
                {
                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tellWithCard(handlerInput, "CIP Query","The total number of " + primaryContentType + " between " + formatDateToAmerican(startDate) + " and " + formatDateToAmerican(endDate) + " is equal to " + jsonOutput.getString("Output0"));
                }
                else
                {
                    return Utils.retrieveServletErrors(handlerInput, jsonOutputString);
                }
                //[/Servlet Output Parsing]
                //[/Connect to Servlet]
            } catch (Exception e) {
                speechText = "Error on output: " + e.toString();
            }
            printLog("Error: Something went horribly wrong in getGenericGetFirst: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getGenericGetFirst: " + speechText);
        }
    }
}
