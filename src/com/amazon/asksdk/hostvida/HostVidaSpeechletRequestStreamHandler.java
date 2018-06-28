/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.asksdk.hostvida;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.amazon.asksdk.hostvida.Handlers.VidaHandlers.getDoormanWelcomeResponseHandler;
import com.amazon.asksdk.hostvida.Handlers.VidaHandlers.getVacantRoomsResponseHandler;
import com.amazon.asksdk.hostvida.Handlers.general.CancelandStopIntentHandler;
import com.amazon.asksdk.hostvida.Handlers.general.LaunchRequestHandler;
import com.amazon.asksdk.hostvida.Handlers.general.SessionEndedRequestHandler;
import com.amazon.asksdk.hostvida.Handlers.general.allExceptionsHandler;


/**
 * This class could be the handler for an AWS Lambda function powering an Alexa Skills Kit
 * experience.
 */
public final class HostVidaSpeechletRequestStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard().addRequestHandlers(
                                                    new CancelandStopIntentHandler(),
                                                    new LaunchRequestHandler(),
                                                    new SessionEndedRequestHandler(),
                                                    new getVacantRoomsResponseHandler(),
                                                    new getDoormanWelcomeResponseHandler()
                                                    )
                .addExceptionHandler(new allExceptionsHandler()).build();
    }

    public HostVidaSpeechletRequestStreamHandler() {
        super(getSkill());
    }
}
