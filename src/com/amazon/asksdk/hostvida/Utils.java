package com.amazon.asksdk.hostvida;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.*;
import com.amazon.ask.model.slu.entityresolution.Resolution;
import com.amazon.ask.model.slu.entityresolution.ValueWrapper;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by owenryan on 04/04/2018.
 */
public class Utils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HostVidaSpeechletRequestStreamHandler.class);

    /**
     * connect to a servlet using HTTP.
     * @param urlString the url for the servlet(and all the parameters)
     * @return the string response from the servlet
     */
    public static String requestHTTP(String urlString) {
        String speechOutput = "requestHTTP Placeholder Speech Output";

        //[Connection]
        InputStreamReader inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder builder = new StringBuilder();
        HttpURLConnection myURLConnection = null;

        try {
            URL url = new URL(urlString);
            myURLConnection = (HttpURLConnection)url.openConnection();
            String userCredentials = "uat:m0nd8y";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            myURLConnection.setRequestProperty ("Authorization", basicAuth);
            InputStream servletInputStream = myURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(servletInputStream));
            StringBuilder streamOutputBuilder = new StringBuilder();
            String lineOfStreamOutput;
            while((lineOfStreamOutput = reader.readLine()) != null) {
                streamOutputBuilder.append(lineOfStreamOutput);
            }

            speechOutput = streamOutputBuilder.toString();
        } catch (UnsupportedEncodingException e){
            speechOutput = "UnsupportedEncodingException: " + e.toString();
            return speechOutput;
        } catch (IOException e) {
            // reset builder to a blank string
            //builder.setLength(0);
            speechOutput = "IO EXCEPTION: " + e.toString();
            return speechOutput;
        } catch (NullPointerException e)
        {
            speechOutput = "Null Pointer Exception: " + e.toString();
            return speechOutput;
        }
        finally {

            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(bufferedReader);
            myURLConnection.disconnect();
        }
        //[/Connection]
        return speechOutput;
    }

    /**
     * Helper method to check for missing inputs, and handle user confirmation of the current query.
     * @param inputs the inputs taken from the intent's slots that must be confirmed to not be missing.
     * @param handlerInput the object containing all information about the alexa request, necessary for creating SpeechletResponses.
     *
     * @return SpeechletResponse: returns null if all of the checks are passed. Otherwise returns either a
     * SpeechletResponse confirming the user's refusal to confirm the current query, containing either a
     * call to delegate dialog to the alexa interaction model or returns an error message telling the user what went wrong.
     */
    public static Optional<Response> handleInputsChecking(String[] inputs, HandlerInput handlerInput)
    {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();

        printLog("Commencing input checking");
        for (String aInput: inputs)
        {
            if(aInput == null)
            {
                try {
                    printLog("A slot was null, delegating dialog to alexa skill.");
                    if(((IntentRequest)handlerInput.getRequestEnvelope().getRequest()).getDialogState() != DialogState.COMPLETED) {
                        return delegate(handlerInput);
                    }
                    else
                    {
                        return tell(handlerInput, "Error: at least one of your inputs was null.");
                    }
                } catch (Exception e) {
                    return tell(handlerInput, "Error delegating for slots: " + e.toString());
                }
            }
        }

        printLog("slot checking complete, proceeding with Intent Response Logic.");
        return null; //returning null means that the check was successful
    }

    /**
     * Helper method to check for missing inputs, and handle user confirmation of the current query.
     * @param inputs the inputs taken from the intent's slots that must be confirmed to not be missing.
     * @param handlerInput the object containing all information about the alexa request, necessary for creating SpeechletResponses.
     *
     * @return SpeechletResponse: returns null if all of the checks are passed. Otherwise returns either a
     * SpeechletResponse confirming the user's refusal to confirm the current query, containing either a
     * call to delegate dialog to the alexa interaction model or returns an error message telling the user what went wrong.
     */
    public static Optional<Response> handleInputsAndIntentConfirmation(String[] inputs, HandlerInput handlerInput)
    {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();

        printLog("Commencing input and intent confirmation checking");
        for (String aInput: inputs)
        {
            if(aInput == null)
            {
                try {
                    printLog("A slot was null, delegating dialog to alexa skill.");
                    return delegate(handlerInput);
                } catch (Exception e) {
                    return tell(handlerInput, "Error delegating for slots: " + e.toString());
                }
            }
        }

        if (intent.getConfirmationStatus() == IntentConfirmationStatus.DENIED)
        {
            printLog("Intent Confirmation denied, aborting request.");
            return tell(handlerInput, "Understood, canceling request.");
            /**
             * the Confirmation Intent for Alexa skills is intended to be the final check and will not allow further dialog beyond this point,
             * meaning only tell may be used.
             */
        }
        // if slots not yet confirmed, delegate confirmation to the dialog model.
        else if(intent.getConfirmationStatus() != IntentConfirmationStatus.CONFIRMED)
        {
            printLog("Intent not yet confirmed, delegating dlialog to alexa skill.");
            return delegate(handlerInput);
        }
        else
        {
            printLog("Intent confirmation confirmed, proceeding with Intent Response Logic.");
            return null; //returning null means that the check was successful
        }
    }

    /**
     * Helper method for retrieving a Tell response
     * @return  speech text.
     */
    public static Optional<Response> tell(HandlerInput input, String speechText)
    {
        return input.getResponseBuilder().withSpeech(speechText).withShouldEndSession(true).build();
    }

    /**
     * Helper method for retrieving a Tell response
     * @return  speech text.
     */
    public static Optional<Response> tellWithCard(HandlerInput input, String speechText, String cardTitle, String cardText)
    {
        return input.getResponseBuilder().withSpeech(speechText).withSimpleCard(cardTitle, cardText).withShouldEndSession(true).build();
    }

    /**
     * Helper method for retrieving a Tell response
     * @return  speech text.
     */
    public static Optional<Response> tellWithCard(HandlerInput input, String cardTitle, String speechText)
    {
        return input.getResponseBuilder().withSpeech(speechText).withSimpleCard(cardTitle, speechText).withShouldEndSession(true).build();
    }

    /**
     * Helper method for retrieving a Tell response
     * @return  speech text.
     */
    public static Optional<Response> midtell(HandlerInput input, String speechText)
    {
        return input.getResponseBuilder().withSpeech(speechText).withShouldEndSession(false).build();
    }

    /**
     * Helper method for retrieving an Elicit response that asks the user to fill a slot. e.g. "how many shoes did you order?"
     * @return  speech text.
     */
    public static Optional<Response> elicit(HandlerInput input, String slotToElicit, String promptSpeech) {
        IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();

        return input.getResponseBuilder().withSpeech(promptSpeech)
                    .addElicitSlotDirective(slotToElicit, request.getIntent())
                    .withShouldEndSession(false)
                    .build();
    }

    /**
     * Helper method for delegating dialog to the Alexa skill's Dialog Model.
     * @param input
     * @return a speechletResponse containing a delegate directive, that delagates dialog to the alexa skill's dialog model.
     */
    public static Optional<Response> delegate(HandlerInput input) {
        printLog("Attempting to Delegate with dialog state: " + ((IntentRequest)input.getRequestEnvelope().getRequest()).getDialogState());
        IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();

        return input.getResponseBuilder()
                    .addDelegateDirective(request.getIntent())
                    .withShouldEndSession(false)
                    .build();
    }

    /**
     * Returns true if NONE of the Slots in the intent are null. (Intent does not have to be required slots)
     * @param anIntent
     * @return true if all slots are filled. False if at least one slot is null.
     */
    public static boolean allSlotsFilled(Intent anIntent)
    {
        if(anIntent.getSlots().size() < 1)
        {
            return true;
        }
        else
        {
            ArrayList<Slot> slots = new ArrayList<Slot>(anIntent.getSlots().values());

            for (Slot aSlot : slots) {
                if (aSlot.getValue() == null) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Prints a string to cloudLogs with the Custom Debug Tag
     * @param text
     */
    public static void printLog(String text)
    {
        log.debug(text);
        System.out.println("Custom Debug: " + text);
    }

    /**
     * Translates Alexa Formated Dates to the format day/month/year used by Vida Servlet.
     * Note: The only possible formats for alexa dates not covered are decades and seasons.
     * @param inDate
     * @return a string for the Date in the format dd/MM/yyyy.
     * @throws ParseException
     */
    public static String formatDate(String inDate) throws ParseException {
        //convert from year month and day. e.g. the 20th of june, 2012
        try
        {
            SimpleDateFormat alexaFormat = new SimpleDateFormat("yyyy-MM-dd");
            alexaFormat.setLenient(false);
            Date dateObject = alexaFormat.parse(inDate);
            SimpleDateFormat servletFormat = new SimpleDateFormat("dd/MM/yyyy");
            return servletFormat.format(dateObject);
        }
        catch (ParseException e)
        {
            //convert from year and month. e.g. this month
            try
            {
                SimpleDateFormat alexaFormat = new SimpleDateFormat("yyyy-MM");
                alexaFormat.setLenient(false);
                Date dateObject = alexaFormat.parse(inDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateObject); // your date (java.util.Date)
                cal.set(Calendar.DAY_OF_MONTH, 1); // You can -/+ x months here to go back in history or move forward.
                SimpleDateFormat servletFormat = new SimpleDateFormat("dd/MM/yyyy");
                return servletFormat.format(cal.getTime());
            }
            catch (ParseException e2)
            {
                //Convert from year and week. e.g. this week
                try
                {
                    SimpleDateFormat alexaFormat = new SimpleDateFormat("yyyy-'W'ww");
                    alexaFormat.setLenient(false);
                    Date dateObject = alexaFormat.parse(inDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateObject); // your date (java.util.Date)
                    SimpleDateFormat servletFormat = new SimpleDateFormat("dd/MM/yyyy");
                    return servletFormat.format(cal.getTime());
                }
                catch (ParseException e3)
                {
                    //Convert from year. e.g. 1997
                    try
                    {
                        if(inDate.charAt(3) == 'X')
                        {
                            inDate = inDate.replace("X", "0");
                        }
                        SimpleDateFormat alexaFormat = new SimpleDateFormat("yyyy");
                        alexaFormat.setLenient(false);
                        Date dateObject = alexaFormat.parse(inDate);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dateObject); // your date (java.util.Date)
                        cal.set(Calendar.MONTH, Calendar.JANUARY);
                        cal.set(Calendar.DAY_OF_MONTH, 1); // You can -/+ x months here to go back in history or move forward.
                        SimpleDateFormat servletFormat = new SimpleDateFormat("dd/MM/yyyy");
                        return servletFormat.format(cal.getTime());
                    }
                    catch (ParseException e4)
                    {
                        printLog("Error Parsing Date to Servlet Format: " + e4.toString());
                        throw e4;
                    }
                }
            }
        }
    }

    /**
     * Translates dates of format day/month/year used by Vida Servlet to American Formated Dates spoken by Alexa.
     * As the Alexa reads dates as american dates so the Month must come before the day.
     * @param inDate
     * @return a string for the Date in the format MM-dd-yyyy.
     * @throws ParseException
     */
    public static String formatDateToAmerican(String inDate) throws ParseException {
        //convert from year month and day. e.g. the 20th of june, 2012
        try
        {
            SimpleDateFormat serlvetFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateObject = serlvetFormat.parse(inDate);
            SimpleDateFormat alexaAmericanFormat = new SimpleDateFormat("MM-dd-yyyy");
            return alexaAmericanFormat.format(dateObject);
        }
        catch (ParseException e)
        {
            throw e;
        }
    }

    /**
     * TODO: REMOVE THIS METHOD.
     * This method should be used only as a temporary solution to inaccurate slot values, the proper solution is to update the
     * project to V2 of the java ask sdk and use slot.getID to take inputs instead of slot.getvalue.
     * @param primaryIn
     * @return
     */
    public static String formatPrimaryContentType(String primaryIn)
    {
        switch (primaryIn)
        {
            case "quote request": return "QuoteRequest";
            case "Quote request": return "QuoteRequest";
            case "Quote Request": return "QuoteRequest";
            case "quote Request": return "QuoteRequest";
            default: return primaryIn;
        }
    }

    public static String getSlotID(Slot slotIn)
    {
        try {
            for (Resolution aRes : slotIn.getResolutions().getResolutionsPerAuthority()) {
                for (ValueWrapper aWrap : aRes.getValues()) {
                    return aWrap.getValue().getId();
                }
            }
            return slotIn.getValue();
        }
        catch (NullPointerException e)
        {
            return null;
        }
    }

    public static Optional<Response> retrieveServletErrors(HandlerInput input, String jsonOutputString)
    {
        if(jsonOutputString.contains("Server returned HTTP response code: 502"))
        {
            return tell(input,"I'm sorry but there was a 502 Error: Bad Gateway when attempting to contact the server.");
        }


        if(jsonOutputString.charAt(0) == '{')
        {
            try
            {
                JSONObject jsonOutput = new JSONObject(jsonOutputString);
                if (jsonOutput.has("ErrorMsg"))
                {
                    if(jsonOutput.getString("ErrorMsg").startsWith("Error:"))
                    {
                        //servlet returned a error as its response
                        return tell(input, "I'm sorry but the servlet has suffered an internal error. Playing error message: " + jsonOutput.getString("ErrorMsg"));
                    }
                    else if(jsonOutput.getString("ErrorMsg").startsWith("Exception:"))
                    {
                        //servlet returned a error as its response
                        return tellWithCard(input, "I'm sorry but the servlet has thrown an internal Exception. Playing Exception message: " + jsonOutput.getString("ErrorMsg"), "Servlet Internal Exception", jsonOutput.getString("Error"));
                    }
                    else
                    {
                        return tell(input, "I'm sorry but the servlet has thrown an internal error or excpetion. But the response has not specified whether it was an error or exception. The response was: " + jsonOutput.getString("ErrorMsg"));
                    }
                }
                else
                {
                    //all is well, no error detected
                    return null;
                }
            }
            catch (JSONException e)
            {
                return tell(input, "I'm sorry but there was JSON Exception while trying to identify errors. Playing Exception Message: " + e.getMessage().toString() + " for input: " + jsonOutputString);
            }
        }
        else
        {
            //Non-json response received from servlet
            return tell(input, "I'm sorry but the server returned an unexpected response, the response received was " + jsonOutputString);
        }
    }
}

/*

 */