/*
 * jon.knight@forgerock.com
 *
 * Dumps shared state to debug
 *
 */

/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2017 ForgeRock AS.
 */

package org.forgerock.openam.auth.nodes;

import com.google.inject.assistedinject.Assisted;
import com.sun.identity.shared.debug.Debug;
import org.forgerock.guava.common.collect.ImmutableList;
import org.forgerock.json.JsonValue;
import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.Action;
import org.forgerock.openam.auth.node.api.Node;
import org.forgerock.openam.auth.node.api.SingleOutcomeNode;
import org.forgerock.openam.auth.node.api.TreeContext;

import javax.inject.Inject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import java.util.*;
import java.text.SimpleDateFormat;


import static org.forgerock.openam.auth.node.api.Action.send;


@Node.Metadata(outcomeProvider = SingleOutcomeNode.OutcomeProvider.class,
            configClass = DebugNode.Config.class)

public class DebugNode extends SingleOutcomeNode {

    public interface Config {
        @Attribute(order = 100)
        default boolean transientState() { return false; }
    }

    private final static String DEBUG_FILE = "DebugNode";
    protected Debug debug = Debug.getInstance(DEBUG_FILE);

    private final DebugNode.Config config;

    /**
     * @param config Node configuration.
     */
    @Inject
    public DebugNode(@Assisted DebugNode.Config config) {
        this.config = config;
    }


    @Override
    public Action process(TreeContext context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();
        String output = "[" + DEBUG_FILE + "]: " + dateFormat.format(date);
        output += "\n---------------------------------------";
        output += "\n" + "Shared state        : " + context.sharedState.toString();
        if (config.transientState()) output += "\n" + "Transient state     : " + context.transientState.toString();
        else output += "\n" + "Transient state     : [ unavailable ]";
        output += "\n" + "Request headers     : " + context.request.headers.toString();
        output += "\n" + "Request clientIp    : " + context.request.clientIp.toString();
        output += "\n" + "Request hostName    : " + context.request.hostName.toString();
        output += "\n" + "Request ssoTokenId  : " + context.request.ssoTokenId.toString();
        output += "\n" + "Request cookie      : " + context.request.cookies.toString();
        output += "\n" + "Request parameters  : " + context.request.parameters.toString();
        output += " \n";
        debug.error(output);
        return goToNext().build();
    }
}

