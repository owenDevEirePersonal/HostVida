package com.amazon.asksdk.hostvida.Handlers.genericVidaHandlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.*;
import com.amazon.asksdk.hostvida.Utils;
import com.amazonaws.util.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;

import static com.amazon.asksdk.hostvida.Constants.*;
import static com.amazon.asksdk.hostvida.Utils.*;

/**
 * Created by owenryan on 06/04/2018.
 */



public class getFullGenericFunctionResponseHandler implements RequestHandler{
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches(request_GenericFullRequest))
        {
            return true;
        }
        return false;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();

        String speechText = "";

        String startDate = intent.getSlots().get(slot_startDate).getValue();
        String endDate = intent.getSlots().get(slot_endDate).getValue();
        String dateName = getSlotID(intent.getSlots().get(slot_dateName));


        String auth = "CitAuth";
        String primaryContentType = getSlotID(intent.getSlots().get(slot_PrimaryContentType));
        String conditions = "";
        String resultInstructions = getSlotID(intent.getSlots().get(slot_result));
        String hasMoreRequirements = getSlotID(intent.getSlots().get(slot_NoMoreRequirements));
        String currentRequirementKey = getSlotID(intent.getSlots().get(slot_requirementKey));
        String currentRequirementValue = intent.getSlots().get(slot_requirementValue).getValue();

        try {
            //[Ensure that all slots and inputs are filled(except requirements)]
            //errorResponse is null if no inputs have caused errors
            Optional<Response> errorResponse = handleInputsChecking(new String[]{startDate, endDate, dateName, primaryContentType, resultInstructions, hasMoreRequirements}, handlerInput);

            if (errorResponse != null)
            {
                printLog("Post Deligate, error: " + startDate + ", " + endDate + ", " + dateName + ", " + primaryContentType + ", " + resultInstructions + ", " + hasMoreRequirements + ", " + currentRequirementKey + ", " + currentRequirementValue);
                return errorResponse;
            }
            else
            {

                //if any of the main inputs are null then the user will be told which input they entered was invalid.
                if(!anyNull(new String[]{startDate, endDate, dateName, primaryContentType, resultInstructions, hasMoreRequirements}).matches("No Null Strings Found"))
                {
                    return tell(handlerInput, "Error: The input: " + anyNull(new String[]{startDate, endDate, dateName, primaryContentType, resultInstructions, hasMoreRequirements}) + " was null. Its possible that you entered a value that the schema could not recognise, please contact a developer.");
                }

                //if the user has previously said yes to having more requirements then enter the Requirements Dialog Logic
                if(hasMoreRequirements.matches("true"))
                {
                    return handleRequirementsDialog(handlerInput, currentRequirementKey, currentRequirementValue);
                }

                printLog("All Inputs handled, proceeding to http request logic with the following inputs: " + "startDate: " + startDate + ", endDate: " + endDate + ", dateName: " + dateName + ", primaryContentType: " + primaryContentType + ", resultInstructions: " + resultInstructions + ", hasMoreRequirements: " + hasMoreRequirements );

                try {
                    startDate = formatDate(startDate);
                    endDate = formatDate(endDate);

                    //[Json formatting]
                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put(auth_input_AuthType, auth);
                    jsonInput.put(input_generic_dateName, dateName);
                    jsonInput.put(input_generic_startDate, startDate);
                    jsonInput.put(input_generic_endDate, endDate);
                    jsonInput.put(input_generic_primaryContentType, primaryContentType);
                    jsonInput.put(input_generic_Conditions, conditions);
                    jsonInput.put(input_generic_result, resultInstructions);

                    if(handlerInput.getAttributesManager().getSessionAttributes().get(sessionAtt_genericGettingRequirements) != null) {
                        Map<String, String> allSavedRequirements = (Map<String, String>) handlerInput.getAttributesManager().getSessionAttributes().get(sessionAtt_genericGettingRequirements);
                        JSONObject jsonRequirements = new JSONObject();
                        for (Map.Entry<String, String> aEntry : allSavedRequirements.entrySet()) {
                            if (!aEntry.getKey().matches(genericGettingRequirementsDummyKey)) {
                                jsonRequirements.put(aEntry.getKey(), "'" + aEntry.getValue() + "'");
                            }
                        }
                        jsonInput.put(input_generic_Requirements, jsonRequirements);
                    }

                    JSONObject jsonCustom = new JSONObject();
                    jsonCustom.put(auth_input_signInEmail, userEmail);
                    jsonCustom.put(auth_input_signInPassword, userPassword);
                    jsonInput.put(input_Custom, jsonCustom);
                    //[/Json formatting]

                    //[Connect to Servlet]
                    printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString() + " only its been encoded in UTF-8");
                    String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                    //Note if this method returns a fileNotFound Exception it is possible that conditions or requirements make reference to a class that does not exist in the dari database.

                    //[Servlet Output Parsing]
                    if(Utils.retrieveServletErrors(handlerInput, jsonOutputString) == null)
                    {
                        String out = "";
                        JSONObject jsonOutput = new JSONObject(jsonOutputString);
                        for (int i = 0; i < jsonOutput.length(); i++) {
                            if (i > 0) {
                                out += ("," + jsonOutput.get("Output" + i));
                            } else {
                                out += (jsonOutput.get("Output0"));
                            }
                        }
                        return tellWithCard(handlerInput, "CIP Query","The answer to your query is " + out);
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
        catch (Exception e)
        {
            return tell(handlerInput, "There was an error during while handling the request: " + e.toString() + " " + e.getMessage());
        }
    }

    //return the name of the first String that's null.
    private String anyNull(String[] inString)
    {
        int i = 0;
        for (String a: inString) {
            if(a == null)
            {
                switch (i)
                {
                    case 0: return "startDate";
                    case 1: return "endDate";
                    case 2: return "dateName";
                    case 3: return "primaryContentType";
                    case 4: return "resultInstructions";
                    case 5: return "hasMoreRequirements";
                    default: return "You shouldn't see this, anyNull should only be used in one spot.";
                }
            }
            i++;
        }
        return "No Null Strings Found";
    }

    private Optional<Response> handleRequirementsDialog(HandlerInput handlerInput, String currentRequirementKey, String currentRequirementValue)
    {
        Map<String, Object> attMap = handlerInput.getAttributesManager().getSessionAttributes();
        Map<String, String> savedRequirements = (Map<String, String>) attMap.get(sessionAtt_genericGettingRequirements);

        //Initialize the session variables if they dob't exist
        if(attMap.get(sessionAtt_genericGettingRequirementsStage) == null)
        {
            printLog("session variables missing, fixing now.");
            attMap.put(sessionAtt_genericGettingRequirementsStage, sessionAttValue_getReqGetKey);

            savedRequirements = new HashMap<String, String>();
            savedRequirements.put(genericGettingRequirementsDummyKey, "DummyValue: This key:value pair should not be seen by the user and is intended as a dummy value to allow the requirements map to be saved as a session attribute, as alexa session attributes will not allow empty maps to be saved");
            attMap.put(sessionAtt_genericGettingRequirements, savedRequirements);

            handlerInput.getAttributesManager().setSessionAttributes(attMap);
        }


        //[elicit the requirement's key, value or whether there are more requirements to add]
        Intent intent = ((IntentRequest)handlerInput.getRequestEnvelope().getRequest()).getIntent();
        switch (handlerInput.getAttributesManager().getSessionAttributes().get(sessionAtt_genericGettingRequirementsStage).toString())
        {
            case sessionAttValue_getReqGetKey:
                attMap.replace(sessionAtt_genericGettingRequirementsStage, sessionAttValue_getReqGetValue);
                handlerInput.getAttributesManager().setSessionAttributes(attMap);
                return handlerInput.getResponseBuilder().withSpeech("What is the name of the variable?").addElicitSlotDirective(slot_requirementKey, intent).build();

            case sessionAttValue_getReqGetValue:
                attMap.replace(sessionAtt_genericGettingRequirementsStage, sessionAttValue_getReqGetHasNext);
                handlerInput.getAttributesManager().setSessionAttributes(attMap);
                return handlerInput.getResponseBuilder().withSpeech("What is the desired value of that variable?").addElicitSlotDirective(slot_requirementValue, intent).build();

            case sessionAttValue_getReqGetHasNext:
                printLog("Adding to savedRequirements: " + currentRequirementKey + ":" + currentRequirementValue);
                savedRequirements.put(currentRequirementKey, currentRequirementValue);

                attMap.replace(sessionAtt_genericGettingRequirementsStage, sessionAttValue_getReqGetKey);
                attMap.replace(sessionAtt_genericGettingRequirements, savedRequirements);
                handlerInput.getAttributesManager().setSessionAttributes(attMap);
                return handlerInput.getResponseBuilder().withSpeech("Are there any more conditions for the query?").addElicitSlotDirective(slot_NoMoreRequirements, intent).build();
        }
        return tell(handlerInput, "Error: sessionAtt_genericGettingRequirementsStage does not match any know stage. This should not be possible.");
    }
}
