/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.asksdk.cqevida;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;

import com.amazonaws.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.amazon.asksdk.cqevida.Utils.*;




public class CQEVidaSpeechlet implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(CQEVidaSpeechlet.class);

    //TODO: !!!!!CHANGE THIS SITE ADDRESS WHEN PUSHING TO LIVE!!!!!
    private static final String siteAddress = "http://qa.cip.ops.deveire.com";

    //private static final String siteAddress = "http://localhost:8080";
    private static final String userEmail = "owen@databots360.com";
    private static final String userPassword = "m0nd8y";

    private static final String input_Custom = "Custom";
    private static final String input_requestType = "requestType";
    private static final String input_insurer = "inputInsurerName";
    private static final String input_startDate = "inputStartDateTime";
    private static final String input_endDate = "inputEndDateTime";

    private static final String auth_input_AuthType = "Auth";
    private static final String auth_input_signInEmail = "signInEmail";
    private static final String auth_input_signInPassword = "signInPassword";

    private static final String input_generic_dateName = "dateName";
    private static final String input_generic_startDate = "startDate";
    private static final String input_generic_endDate = "endDate";
    private static final String input_generic_primaryContentType = "PrimaryContentType";
    private static final String input_generic_result = "result";
    private static final String input_generic_Conditions = "Conditions";
    private static final String input_generic_Requirements = "Requirements";

    private static final String slot_Insurer = "insurer";
    private static final String slot_startDate = "startDate";
    private static final String slot_endDate = "endDate";
    private static final String slot_sector = "sector";
    private static final String slot_PrimaryContentType = "primaryContentType";

    private static final String request_NumberOfQuotesByInsurerAndTime = "requestNumberOfQuotesByInsurerAndTime";
    private static final String request_NumberOfQuotesByTime = "requestNumberOfQuotesByTime";
    private static final String request_NumberOfQuotesBySector = "requestNumberOfQuotesBySector";
    private static final String request_RevenueByInsurerAndTime = "requestRevenueByInsurerAndTime";
    private static final String request_RevenueByTime = "requestRevenueByTime";
    private static final String request_GenericGetFirst = "genericGetFirst";
    private static final String request_GenericRequest = "genericRequest";

    @Override
    public boolean canHandle(HandlerInput input) {
        //return input.matches(Predicates.requestType(LaunchRequest.class));
        //return true;
        return false;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        return onIntent(handlerInput);
    }


    /*@Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        log.info("OnSessionStarted");
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());
        printLog("Test");
        return getWelcomeResponse();
    }*/

    public Optional<Response> onIntent(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Session currentSession = handlerInput.getRequestEnvelope().getSession();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), handlerInput.getRequestEnvelope().getSession().getSessionId());
        log.info("OnIntent");

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        printLog("Choosing Response to Intent: " + intentName);
        switch (intentName)
        {
            case "launch": return getTestResponse("Launch!", handlerInput);
            case request_NumberOfQuotesByInsurerAndTime: return getNumberOfQuotesByInsurerAndTimeResponse(handlerInput);
            case request_NumberOfQuotesByTime: return getNumberOfQuotesByTimeResponse(handlerInput);
            case request_NumberOfQuotesBySector: return getNumberOfQuotesBySectorResponse(handlerInput);
            case request_RevenueByInsurerAndTime: return getRevenueByInsurerAndTimeResponse(handlerInput);
            case request_RevenueByTime: return getRevenueByTimeResponse(handlerInput);
            case request_GenericGetFirst: return getGenericGetFirstResponse(handlerInput);
            case request_GenericRequest: return getGenericFunctionResponse(handlerInput);
            default: return tell(handlerInput, "This is unsupported.  Please try something else. The Intent you provided was: " + intent.getName());
        }
    }

    //@Override
    public void onSessionEnded(HandlerInput handlerInput) {
        log.info("onSessionEnded requestId={}, sessionId={}", handlerInput.getRequestEnvelope().getRequest().getRequestId(),
                handlerInput.getRequestEnvelope().getSession().getSessionId());
        // any cleanup logic goes here
        log.info("OnSessionEnded");
    }

 //[ Response Generation Methods]

    private Optional<Response> getTestResponse(String inText, HandlerInput handlerInput) {
        String speechText = "Test Successful: " + inText;
        return tell(handlerInput, speechText);
    }

    private Optional<Response> getNumberOfQuotesByInsurerAndTimeResponse(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        String insurer = getSlotID(intent.getSlots().get(slot_Insurer));
        String startDate = intent.getSlots().get(slot_startDate).getValue();
        String endDate = intent.getSlots().get(slot_endDate).getValue();

        Optional<Response> errorResponse = handleInputsAndIntentConfirmation(new String[]{insurer, startDate, endDate}, handlerInput);
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
                JSONObject jsonCustom = new JSONObject();
                JSONObject jsonInput = new JSONObject();
                jsonCustom.put(input_requestType, request_NumberOfQuotesByInsurerAndTime);
                jsonCustom.put(input_insurer, insurer);
                jsonCustom.put(input_startDate, startDate);
                jsonCustom.put(input_endDate, endDate);
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                jsonInput.put(auth_input_AuthType, "CitAuth");
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString());
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + jsonInput.toString());
                //[Servlet Output Parsing]
                if(jsonOutputString.charAt(0) == '{') {
                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tell(handlerInput, "There have been " + jsonOutput.getString("outputQuoteCount") + " quotes from " + insurer + " between " + formatDateToAmerican(startDate) + " and " + formatDateToAmerican(endDate));
                }
                else
                {
                    return tell(handlerInput, "Error getting output: " + jsonOutputString);
                }
                //[/Servlet Output Parsing]
                //[/Connect to Servlet]
            } catch (Exception e) {
                speechText = "Error on output: " + e.toString();
            }
            printLog("Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
        }

    }

    private Optional<Response> getNumberOfQuotesByTimeResponse(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        String startDate = intent.getSlots().get(slot_startDate).getValue();
        String endDate = intent.getSlots().get(slot_endDate).getValue();

        Optional<Response> errorResponse = handleInputsAndIntentConfirmation(new String[]{startDate, endDate}, handlerInput);
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
                JSONObject jsonCustom = new JSONObject();
                JSONObject jsonInput = new JSONObject();
                jsonCustom.put(input_requestType, request_NumberOfQuotesByTime);
                jsonCustom.put(input_startDate, startDate);
                jsonCustom.put(input_endDate, endDate);
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                jsonInput.put(auth_input_AuthType, "CitAuth");
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString());
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + jsonInput.toString());//[Servlet Output Parsing]
                if(jsonOutputString.charAt(0) == '{') {

                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tell(handlerInput, "There have been " + jsonOutput.getString("outputQuoteCount") + " quotes given between " + formatDateToAmerican(startDate) + " and " + formatDateToAmerican(endDate));
                }
                else
                {
                    return tell(handlerInput, "Error getting output: " + jsonOutputString);
                }
                //[/Servlet Output Parsing]
                //[/Connect to Servlet]
            } catch (Exception e) {
                    speechText = "Error on output: " + e.toString();
            }
            printLog("Error: Something went horribly wrong in getNumberOfQuotesByTimeResponseHandler: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getNumberOfQuotesByTimeResponseHandler: " + speechText);
        }

    }

    private Optional<Response> getNumberOfQuotesBySectorResponse(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        String sector = getSlotID(intent.getSlots().get(slot_sector));

        Optional<Response> errorResponse = handleInputsAndIntentConfirmation(new String[]{sector}, handlerInput);
        if(errorResponse != null)
        {
            return errorResponse;
        }
        else
        {
            try {

                //[Json formatting]
                JSONObject jsonCustom = new JSONObject();
                JSONObject jsonInput = new JSONObject();
                jsonCustom.put(input_requestType, request_NumberOfQuotesBySector);
                jsonCustom.put("inputSectorName", sector);
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                jsonInput.put(auth_input_AuthType, "CitAuth");
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString());
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + jsonInput.toString());
                //[Servlet Output Parsing]
                if(jsonOutputString.charAt(0) == '{') {
                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tell(handlerInput, "There have been " + jsonOutput.getString("outputQuoteCount") + " quotes issued in the " + sector + " sector.");
                }
                else
                {
                    return tell(handlerInput, "Error getting output: " + jsonOutputString);
                }
                //[/Servlet Output Parsing]
                //[/Connect to Servlet]
            } catch (Exception e) {
                speechText = "Error on output: " + e.toString();
            }
            printLog("Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
        }
    }

    private Optional<Response> getRevenueByInsurerAndTimeResponse(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        String insurer = getSlotID(intent.getSlots().get(slot_Insurer));
        String startDate = intent.getSlots().get(slot_startDate).getValue();
        String endDate = intent.getSlots().get(slot_endDate).getValue();

        Optional<Response> errorResponse = handleInputsAndIntentConfirmation(new String[]{insurer, startDate, endDate}, handlerInput);
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
                JSONObject jsonCustom = new JSONObject();
                JSONObject jsonInput = new JSONObject();
                jsonCustom.put(input_requestType, request_RevenueByInsurerAndTime);
                jsonCustom.put(input_insurer, insurer);
                jsonCustom.put(input_startDate, startDate);
                jsonCustom.put(input_endDate, endDate);
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                jsonInput.put(auth_input_AuthType, "CitAuth");
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString());
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + jsonInput.toString());
                //[Servlet Output Parsing]
                if(jsonOutputString.charAt(0) == '{') {

                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tell(handlerInput, "Total Revenue for " + insurer + " was " + jsonOutput.getString("outputRevenue") + "€ between " + formatDateToAmerican(startDate) + " and " + formatDateToAmerican(endDate));
                }
                else
                {
                    return tell(handlerInput, "Error getting output: " + jsonOutputString);
                }
                //[/Servlet Output Parsing]
                //[/Connect to Servlet]
            } catch (Exception e) {
                speechText = "Error on output: " + e.toString();
            }
            printLog("Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
        }
    }

    private Optional<Response> getRevenueByTimeResponse(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        String startDate = intent.getSlots().get(slot_startDate).getValue();
        String endDate = intent.getSlots().get(slot_endDate).getValue();

        Optional<Response> errorResponse = handleInputsAndIntentConfirmation(new String[]{startDate, endDate}, handlerInput);
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
                JSONObject jsonCustom = new JSONObject();
                JSONObject jsonInput = new JSONObject();
                jsonCustom.put(input_requestType, request_RevenueByTime);
                jsonCustom.put(input_startDate, startDate);
                jsonCustom.put(input_endDate, endDate);
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                jsonInput.put(auth_input_AuthType, "CitAuth");
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString());
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + jsonInput.toString());
                //[Servlet Output Parsing]
                if(jsonOutputString.charAt(0) == '{') {

                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tell(handlerInput, "Total Revenue for all insurers was " + jsonOutput.getString("outputRevenue") + "€ between " + formatDateToAmerican(startDate) + " and " + formatDateToAmerican(endDate));
                }
                else
                {
                    return tell(handlerInput, "Error getting output: " + jsonOutputString);
                }
                //[/Servlet Output Parsing]
                //[/Connect to Servlet]
            } catch (Exception e) {
                speechText = "Error on output: " + e.toString();
            }
            printLog("Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
        }
    }


    private Optional<Response> getGenericGetFirstResponse(HandlerInput handlerInput) {

        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        String startDate = "2017-10-01";
        String endDate = "2018-02-01";
        String dateName = "createdDate";
        String auth = "CitAuth";
        String primaryContentType = "QuoteRequest";
        String conditions = "";
        String resultIntructions = "count,first/name";
        String requirementKey1 = "user/displayName";
        String requirementValue1 = "mike@deveire.com";


        Optional<Response> errorResponse = null;//handleInputsAndIntentConfirmation(new String[]{startDate, endDate}, requestEnvelope);
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
                jsonInput.put(input_generic_primaryContentType, primaryContentType);
                jsonInput.put(input_generic_Conditions, conditions);
                jsonInput.put(input_generic_result, resultIntructions);

                JSONObject jsonRequirements = new JSONObject();
                jsonRequirements.put(requirementKey1, requirementValue1);
                jsonInput.put(input_generic_Requirements, jsonRequirements);

                JSONObject jsonCustom = new JSONObject();
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString());
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + jsonInput.toString());
                //Note if this method returns a fileNotFound Exception it is possible that conditions or requirements make reference to a class that does not exist in the dari database.
                //[Servlet Output Parsing]
                if(jsonOutputString.charAt(0) == '{') {

                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tell(handlerInput, "Count is equal to " + jsonOutput.getString("Output0") + " and first Quote Requests Name was " + jsonOutput.getString("Output1"));
                }
                else
                {
                    return tell(handlerInput, "Error getting output: " + jsonOutputString);
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

    private Optional<Response> getGenericFunctionResponse(HandlerInput handlerInput) {
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
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + jsonInput.toString());
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + jsonInput.toString());
                //Note if this method returns a fileNotFound Exception it is possible that conditions or requirements make reference to a class that does not exist in the dari database.
                //[Servlet Output Parsing]
                if(jsonOutputString.charAt(0) == '{') {

                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tell(handlerInput, "The total number of " + primaryContentType + " between " + formatDateToAmerican(startDate) + " and " + formatDateToAmerican(endDate) + " is equal to " + jsonOutput.getString("Output0"));
                }
                else
                {
                    return tell(handlerInput, "Error getting output: " + jsonOutputString);
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

    //[End of Response Generation Methods]
}

