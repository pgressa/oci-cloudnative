/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package newsletter.subscription;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import java.util.Collections;
import java.util.Map;

@Controller
class SubscriptionController {

    private final MailSender mailSender;

    SubscriptionController(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Post("/subscribe")
    Map<String, String> subscribe(@Body SubscribeRequest subscribeRequest) {
        final String messageID = mailSender.send(
                subscribeRequest.getEmail(),
                "Hello from Mushop",
                "Thanks for confirming your <b>subscription</b>!"
        );

        return Collections.singletonMap("messageId", messageID);
    }
}
