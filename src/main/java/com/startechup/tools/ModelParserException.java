/*
 *  Copyright (2015) StarTechUp Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package com.startechup.tools;

/**
 * Thrown to indicate that the parse has problem on parsing objects
 * e.g. casting org.json.JSONArray into int[]
 */
public class ModelParserException extends Exception {
    public ModelParserException() {
    }

    public ModelParserException(String detailMessage) {
        super(detailMessage);
    }

    public ModelParserException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ModelParserException(Throwable throwable) {
        super(throwable);
    }
}
