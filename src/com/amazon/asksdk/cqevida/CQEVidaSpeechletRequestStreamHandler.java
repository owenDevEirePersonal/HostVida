/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.asksdk.cqevida;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.amazon.asksdk.cqevida.Handlers.general.CancelandStopIntentHandler;
import com.amazon.asksdk.cqevida.Handlers.general.LaunchRequestHandler;
import com.amazon.asksdk.cqevida.Handlers.general.SessionEndedRequestHandler;
import com.amazon.asksdk.cqevida.Handlers.general.allExceptionsHandler;
import com.amazon.asksdk.cqevida.Handlers.VidaHandlers.*;
import com.amazon.asksdk.cqevida.Handlers.genericVidaHandlers.getFullGenericFunctionResponseHandler;
import com.amazon.asksdk.cqevida.Handlers.genericVidaHandlers.getGenericDemoResponseHandler;
import com.amazon.asksdk.cqevida.Handlers.genericVidaHandlers.getGenericFunctionResponseHandler;
import com.amazon.asksdk.cqevida.Handlers.genericVidaHandlers.getGenericGetFirstResponseHandler;


/**
 * This class could be the handler for an AWS Lambda function powering an Alexa Skills Kit
 * experience.
 */
public final class CQEVidaSpeechletRequestStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard().addRequestHandlers(
                                                    new CQEVidaSpeechlet(),
                                                    new CancelandStopIntentHandler(),
                                                    new LaunchRequestHandler(),
                                                    new SessionEndedRequestHandler(),
                                                    new getGenericFunctionResponseHandler(),
                                                    new getGenericGetFirstResponseHandler(),
                                                    new getNumberOfQuotesByInsurerAndTimeResponseHandler(),
                                                    new getNumberOfQuotesBySectorHandler(),
                                                    new getNumberOfQuotesByTimeResponseHandler(),
                                                    new getRevenueByInsurerAndTimeResponseHandler(),
                                                    new getRevenueByTimeResponseHandler(),
                                                    new getLaunchResponseHandler(),
                                                    new getFullGenericFunctionResponseHandler(),
                                                    new getGenericDemoResponseHandler(),
                                                    new getErrorTestHandler()
                                                    )
                .addExceptionHandler(new allExceptionsHandler()).build();
    }

    public CQEVidaSpeechletRequestStreamHandler() {
        super(getSkill());
    }
}
