/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.junit4

import geb.test.CallbackHttpServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class GebReportingTestTest extends GebReportingTest {

    static private counter = 0

    static responseText = """
        <html>
        <body>
            <div class="d1" id="d1">d1</div>
        </body>
        </html>
    """

    def server = new CallbackHttpServer(browser.config)

    @Before
    void setUp() {
        server.start()
        server.get = { req, res ->
            res.outputStream << responseText
        }
        browser.baseUrl = server.baseUrl
        browser.config.reportOnTestFailureOnly = false
        go()
    }

    @Test
    void reportGroupDirIsSetToClassName() {
        assert reportGroupDir.absolutePath.endsWith(getClass().name.replaceAll(/\./, File.separator))
    }

    @Test
    void a() {
        doTestReport()
    }

    @Test
    void b() {
        doTestReport()
    }

    @Test
    void c() {
        doTestReport()
    }

    def doTestReport() {
        if (++counter > 1) {
            def report = reportGroupDir.listFiles().find { it.name.startsWith("00" + (counter - 1)) && endsWith('end.html') }
            assert report.exists()
            assert report.text.contains('<div class="d1" id="d1">')
        }
    }

    @After
    void tearDown() {
        server.stop()
    }
}