/*
 * Copyright (c)  2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.execution.json;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.log4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

import java.util.concurrent.atomic.AtomicInteger;

public class JsonTokenizerAsObjectStreamProcessorFunctionTestCase {
    private static final Logger log = Logger.getLogger(JsonTokenizerAsObjectStreamProcessorFunctionTestCase.class);
    private AtomicInteger count = new AtomicInteger(0);
    private static final String JSON_INPUT = "{emp:[" +
            "{\"name\":\"John\", foo:{fooName:\"fooName\"}, bar:[{barName:\"barName\"},{barName:\"barName2\"}]}," +
            "{\"name\":\"Peter\", foo:{fooName:\"fooName2\"}, bar:[{barName:\"barName3\"},{barName:\"barName4\"}]}" +
            "]}";

    @BeforeMethod
    public void init() {
        count.set(0);
    }

    @Test
    public void testJsonTokenizerWithStringInput() throws InterruptedException, ParseException {
        log.info("JsonTokenizerAsObjectStreamProcessorFunctionTestCase - testJsonTokenizerWithStringInput");
        SiddhiManager siddhiManager = new SiddhiManager();
        String stream = "define stream InputStream(json string,path string);\n";
        String query = ("@info(name = 'query1')\n" +
                "from InputStream#json:tokenizeAsObject(json, path)\n" +
                "select jsonElement\n" +
                "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(stream + query);
        JSONParser jsonParser = new JSONParser();
        Object expectedJsonObject1 = jsonParser.parse("{\"fooName\":\"fooName\"}");
        Object expectedJsonObject2 = jsonParser.parse("{\"barName\":\"barName\"}");
        Object expectedJsonObject3 = jsonParser.parse("{\"barName\":\"barName2\"}");
        Object expectedJsonObject4 = jsonParser.parse("[{\"barName\":\"barName\"},{\"barName\":\"barName2\"}]");
        Object expectedJsonObject5 = jsonParser.parse("[{\"barName\":\"barName3\"},{\"barName\":\"barName4\"}]");
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event event : inEvents) {
                    count.incrementAndGet();
                    switch (count.get()) {
                        case 1:
                            AssertJUnit.assertEquals("John", event.getData(0));
                            break;
                        case 2:
                            AssertJUnit.assertEquals("Peter", event.getData(0));
                            break;
                        case 3:
                            AssertJUnit.assertEquals(expectedJsonObject1, event.getData(0));
                            break;
                        case 4:
                            AssertJUnit.assertEquals(expectedJsonObject2, event.getData(0));
                            break;
                        case 5:
                            AssertJUnit.assertEquals(expectedJsonObject3, event.getData(0));
                            break;
                        case 6:
                            AssertJUnit.assertEquals(expectedJsonObject4, event.getData(0));
                            break;
                        case 7:
                            AssertJUnit.assertEquals(expectedJsonObject5, event.getData(0));
                            break;
                    }
                }
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[0].name"});
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[1].name"});
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[0].foo"});
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[0].bar"});
        inputHandler.send(new Object[]{JSON_INPUT, "$..bar"});
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testJsonTokenizerWithObjectInput() throws InterruptedException, ParseException {
        log.info("JsonTokenizerAsObjectStreamProcessorFunctionTestCase - testJsonTokenizerWithObjectInput");
        SiddhiManager siddhiManager = new SiddhiManager();
        String stream = "define stream InputStream(json object,path string);\n";
        String query = ("@info(name = 'query1')\n" +
                "from InputStream#json:tokenizeAsObject(json, path, false)\n" +
                "select jsonElement\n" +
                "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(stream + query);
        JSONParser jsonParser = new JSONParser();
        Object expectedJsonObject1 = jsonParser.parse("{\"fooName\":\"fooName\"}");
        Object expectedJsonObject2 = jsonParser.parse("{\"barName\":\"barName\"}");
        Object expectedJsonObject3 = jsonParser.parse("{\"barName\":\"barName2\"}");
        Object expectedJsonObject4 = jsonParser.parse("[{\"barName\":\"barName\"},{\"barName\":\"barName2\"}]");
        Object expectedJsonObject5 = jsonParser.parse("[{\"barName\":\"barName3\"},{\"barName\":\"barName4\"}]");
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event event : inEvents) {
                    count.incrementAndGet();
                    switch (count.get()) {
                        case 1:
                            AssertJUnit.assertEquals("John", event.getData(0));
                            break;
                        case 2:
                            AssertJUnit.assertEquals("Peter", event.getData(0));
                            break;
                        case 3:
                            AssertJUnit.assertEquals(expectedJsonObject1, event.getData(0));
                            break;
                        case 4:
                            AssertJUnit.assertEquals(expectedJsonObject2, event.getData(0));
                            break;
                        case 5:
                            AssertJUnit.assertEquals(expectedJsonObject3, event.getData(0));
                            break;
                        case 6:
                            AssertJUnit.assertEquals(expectedJsonObject4, event.getData(0));
                            break;
                        case 7:
                            AssertJUnit.assertEquals(expectedJsonObject5, event.getData(0));
                            break;
                        case 8:
                            AssertJUnit.assertEquals(null, event.getData(0));
                            break;
                    }
                }
            }
        });
        JSONObject jsonObject = (JSONObject) jsonParser.parse(JSON_INPUT);
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{jsonObject, "$.emp[0].name"});
        inputHandler.send(new Object[]{jsonObject, "$.emp[1].name"});
        inputHandler.send(new Object[]{jsonObject, "$.emp[0].foo"});
        inputHandler.send(new Object[]{jsonObject, "$.emp[0].bar"});
        inputHandler.send(new Object[]{jsonObject, "$..bar"});
        inputHandler.send(new Object[]{jsonObject, "$.name"});
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testJsonTokenizerWithFailOnMissingAttribute() throws InterruptedException {
        log.info("JsonTokenizerAsObjectStreamProcessorFunctionTestCase - testJsonTokenizerWithFailOnMissingAttribute");
        SiddhiManager siddhiManager = new SiddhiManager();
        String stream = "define stream InputStream(json string,path string);\n";
        String query = ("@info(name = 'query1')\n" +
                "from InputStream#json:tokenizeAsObject(json, path, false)\n" +
                "select jsonElement\n" +
                "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(stream + query);
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event event : inEvents) {
                    count.incrementAndGet();
                }
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{JSON_INPUT, "$.name"});
        inputHandler.send(new Object[]{JSON_INPUT, "$..xyz"});
        AssertJUnit.assertEquals(2, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testJsonTokenizerWithoutFailOnMissingAttribute() throws InterruptedException {
        log.info("JsonTokenizerAsObjectStreamProcessorFunctionTestCase - testJsonTokenizerWithFailOnMissingAttribute");
        SiddhiManager siddhiManager = new SiddhiManager();
        String stream = "define stream InputStream(json string,path string);\n";
        String query = ("@info(name = 'query1')\n" +
                "from InputStream#json:tokenizeAsObject(json, path)\n" +
                "select jsonElement\n" +
                "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(stream + query);
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event event : inEvents) {
                    count.incrementAndGet();
                }
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{JSON_INPUT, "$.name"});
        inputHandler.send(new Object[]{JSON_INPUT, "$..xyz"});
        AssertJUnit.assertEquals(0, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testJsonTokenizerWithStringInputWithGetString() throws InterruptedException, ParseException {
        log.info("JsonTokenizerAsObjectStreamProcessorFunctionTestCase - testJsonTokenizerWithStringInput");
        SiddhiManager siddhiManager = new SiddhiManager();
        String stream = "define stream InputStream(json string,path string);\n";
        String query = ("@info(name = 'query1')\n" +
                "from InputStream#json:tokenizeAsObject(json, path)\n" +
                "select json:getString(jsonElement,'$') as t\n" +
                "insert into OutputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(stream + query);
        JSONParser jsonParser = new JSONParser();
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents,
                                Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event event : inEvents) {
                    count.incrementAndGet();
                    switch (count.get()) {
                        case 1:
                            AssertJUnit.assertEquals("John", event.getData(0));
                            break;
                        case 2:
                            AssertJUnit.assertEquals("Peter", event.getData(0));
                            break;
                        case 3:
                            AssertJUnit.assertEquals("{\"fooName\":\"fooName\"}", event.getData(0));
                            break;
                        case 4:
                            AssertJUnit.assertEquals("{\"barName\":\"barName\"}", event.getData(0));
                            break;
                        case 5:
                            AssertJUnit.assertEquals("{\"barName\":\"barName2\"}", event.getData(0));
                            break;
                        case 6:
                            AssertJUnit.assertEquals("[{\"barName\":\"barName\"},{\"barName\":\"barName2\"}]",
                                    event.getData(0));
                            break;
                        case 7:
                            AssertJUnit.assertEquals("[{\"barName\":\"barName3\"},{\"barName\":\"barName4\"}]",
                                    event.getData(0));
                            break;
                    }
                }
            }
        });
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[0].name"});
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[1].name"});
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[0].foo"});
        inputHandler.send(new Object[]{JSON_INPUT, "$.emp[0].bar"});
        inputHandler.send(new Object[]{JSON_INPUT, "$..bar"});
        siddhiAppRuntime.shutdown();
    }
}


