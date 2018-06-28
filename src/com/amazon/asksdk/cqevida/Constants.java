package com.amazon.asksdk.cqevida;

import com.amazonaws.services.dynamodbv2.xspec.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owenryan on 06/04/2018.
 */

/**
 *
 * Build command: mvn assembly:assembly -DdescriptorId=jar-with-dependencies package
 *
 * Servlet Test Addresses:
 * -http://qa.cip.ops.deveire.com/vida?jsonIn={"requestType":"requestNumberOfQuotesByInsurerAndTime","inputEndDateTime":"05/02/2018","inputInsurerName":"Isure","inputStartDateTime":"01/10/2017"}&signInPassword=m0nd8y&signInEmail=owen@databots360.com
 * -http://qa.cip.ops.deveire.com/vida?params={"Auth":"CitAuth","Custom":{"requestType":"requestNumberOfQuotesByInsurerAndTime","inputEndDateTime":"02/12/2018","inputInsurerName":"Isure","signInPassword":"m0nd8y","inputStartDateTime":"01/10/2017","signInEmail":"owen@databots360.com"}}
 *
 * Vida Test Phrases:
 * -ask c i p how many quotes were issued by Isure between october 1st 2017 and december 2nd 2018
 * -ask c i p what is the number of quotes given between October 1st 2017 and February 5th 2018
 * -ask c i p how many quotes were issued in the retail sector
 * -ask c i p how much did Isure make between October 1st 2017 and February 5th 2018
 * -ask c i p what was the total revenue of all insurers between October 1st 2017 and February 5th 2018
 *
 * -ask c i p generic Get First
 * -ask c i p to make a generic dari query quote request between October 1st 2017 and February 5th 2018
 *
 * -ask c i p to make a generic request
 *             Quote Request, count, created Date, 3 years ago, today, yes, Display Name, Michael Connolly, no
 *
 * -ask c i p how many quotes has Michael Connolly made
 * -ask c i p how many quotes has Jane Harrington made
 *
 * ask c i p to test error handling
 *
 */

public class Constants {
    public static final Logger log = LoggerFactory.getLogger(CQEVidaSpeechlet.class);

    //TODO: !!!!!CHANGE THIS SITE ADDRESS WHEN PUSHING TO LIVE!!!!!
    public static final String siteAddress = "http://qa.cip.ops.deveire.com";

    //public static final String siteAddress = "http://localhost:8080";
    public static final String userEmail = "owen@databots360.com";
    public static final String userPassword = "m0nd8y";

    public static final String input_Custom = "Custom";
    public static final String input_requestType = "requestType";
    public static final String input_insurer = "inputInsurerName";
    public static final String input_startDate = "inputStartDateTime";
    public static final String input_endDate = "inputEndDateTime";

    public static final String auth_input_AuthType = "Auth";
    public static final String auth_input_signInEmail = "signInEmail";
    public static final String auth_input_signInPassword = "signInPassword";

    public static final String input_generic_dateName = "dateName";
    public static final String input_generic_startDate = "startDate";
    public static final String input_generic_endDate = "endDate";
    public static final String input_generic_primaryContentType = "PrimaryContentType";
    public static final String input_generic_result = "result";
    public static final String input_generic_Conditions = "Conditions";
    public static final String input_generic_Requirements = "Requirements";

    public static final String slot_Insurer = "insurer";
    public static final String slot_startDate = "startDate";
    public static final String slot_endDate = "endDate";
    public static final String slot_sector = "sector";
    public static final String slot_PrimaryContentType = "primaryContentType";
    public static final String slot_result = "result";
    public static final String slot_dateName = "dateName";
    public static final String slot_NoMoreRequirements = "NoMoreRequirements";
    public static final String slot_requirementKey = "requirementKey";
    public static final String slot_requirementValue = "requirementValue";

    public static final String request_NumberOfQuotesByInsurerAndTime = "requestNumberOfQuotesByInsurerAndTime";
    public static final String request_NumberOfQuotesByTime = "requestNumberOfQuotesByTime";
    public static final String request_NumberOfQuotesBySector = "requestNumberOfQuotesBySector";
    public static final String request_RevenueByInsurerAndTime = "requestRevenueByInsurerAndTime";
    public static final String request_RevenueByTime = "requestRevenueByTime";
    public static final String request_GenericGetFirst = "genericGetFirst";
    public static final String request_GenericRequest = "genericRequest";
    public static final String request_GenericFullRequest = "genericFullRequest";
    public static final String request_GenericDemoRequest = "genericDemoRequest";
    public static final String request_ErrorTest = "requestErrorTest";

    public static final String sessionAtt_genericGettingRequirementsStage = "gettingRequirementsStage";
    public static final String sessionAttValue_getReqGetKey = "genericSessionGetKey";
    public static final String sessionAttValue_getReqGetValue = "genericSessionGetValue";
    public static final String sessionAttValue_getReqGetHasNext = "genericSessionGetHasNext";
    public static final String sessionAtt_genericGettingRequirements = "gettingRequirementsRequirements";

    public static final String genericGettingRequirementsDummyKey = "DummyKeyUserShouldNotUse";
}
