package com.katch.core

import com.katch.core.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class KatchTest {

    private val defaultUrl = "http://defaultUrl/with/this/path"
    private val defaultKatch = RealKatch(Config(arrayOf(
            Interceptor("i1", "/this/path",
                    arrayOf(
                            Response("i1r1", 200),
                            Response("i1r2", 200)
                    ),
                    Verb.GET,
                    selectedResponseIndex = 0
            ),
            Interceptor("i2", "/this/path",
                    arrayOf(
                            Response("i2r1", 200),
                            Response("i2r2", 200)
                    ),
                    Verb.POST,
                    selectedResponseIndex = 0
            )
    )))

    @Test
    fun katch_intercept_Match_Verb_but_not_Path() {
        val url = "http://not/matching/path"

        assertNull(defaultKatch.intercept(Request(url, Verb.GET)))
        assertNull(defaultKatch.intercept(Request(url, Verb.POST)))
    }

    @Test
    fun katch_intercept_Match_path_but_only_one_Verb() {
        val katch = RealKatch(Config(
                arrayOf(
                        Interceptor(
                                "i",
                                "/this/path",
                                arrayOf(
                                        Response("i", 200)
                                ),
                                Verb.GET,
                                selectedResponseIndex = 0
                        )
                ))
        )

        val responseOk = katch.intercept(Request(defaultUrl, Verb.GET))
        val responseNull = katch.intercept(Request(defaultUrl, Verb.POST))

        responseOk.assertResponseNotNull()
        assertNull(responseNull)
    }

    @Test
    fun katch_intercept_Match_url_match_per_Verb_and_first_with_Any() {
        val responseI1 = defaultKatch.intercept(Request(defaultUrl, Verb.GET))
        val responseI2 = defaultKatch.intercept(Request(defaultUrl, Verb.POST))
        val responseI1Any = defaultKatch.intercept(Request(defaultUrl, Verb.ANY))

        responseI1.assertResponseNotNull()
        assertEquals("i1r1", responseI1?.name)

        responseI2.assertResponseNotNull()
        assertEquals("i2r1", responseI2?.name)

        responseI1Any.assertResponseNotNull()
        assertEquals("i1r1", responseI1Any?.name)
    }

    @Test
    fun katch_intercept_matcher_Try_each_of_them() {
        val katch = RealKatch(Config(arrayOf(
                Interceptor("i1", "path?", arrayOf(
                        Response("i1r", 201)),
                        Verb.GET,
                        Matcher.REGEX,
                        selectedResponseIndex = 0
                ),
                Interceptor("i2", "/ab/", arrayOf(
                        Response("i2r", 202)),
                        Verb.ANY,
                        Matcher.CONTAINS,
                        selectedResponseIndex = 0
                ),
                Interceptor("i3", "ac/p", arrayOf(
                        Response("i3r", 203)),
                        Verb.ANY,
                        Matcher.END_WITH,
                        selectedResponseIndex = 0
                )
        )))

        val responseI1 = katch.intercept(Request("http://this/pat", Verb.GET))
        val responseI2 = katch.intercept(Request("http://ab/", Verb.GET))
        val responseI3 = katch.intercept(Request("http://ac/p", Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(201, responseI1?.statusCode)
        responseI2.assertResponseNotNull()
        assertEquals(202, responseI2?.statusCode)
        responseI3.assertResponseNotNull()
        assertEquals(203, responseI3?.statusCode)
    }

    /*
        DELAY
     */

    @Test
    fun katch_intercept_no_delay() {
        val katch = RealKatch(Config(arrayOf(
                Interceptor("i0", "/this/path",
                        arrayOf(Response("i0r1", 1)),
                        selectedResponseIndex = 0
                )
        )))

        val responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(1, responseI1?.statusCode)
        assertEquals(0, responseI1?.delay)
    }

    @Test
    fun katch_intercept_delay_response_win() {
        val katch = RealKatch(Config(
                arrayOf(Interceptor("i0", "/this/path",
                        arrayOf(Response("i0r1", 1, delay = 3)),
                        selectedResponseIndex = 0,
                        delay = 2
                )),
                globalDelay = 1
        ))

        val responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(1, responseI1?.statusCode)
        assertEquals(3, responseI1?.delay)
    }

    @Test
    fun katch_intercept_delay_interceptor_win() {
        val katch = RealKatch(Config(
                arrayOf(Interceptor("i0", "/this/path",
                        arrayOf(Response("i0r1", 1)),
                        selectedResponseIndex = 0,
                        delay = 2
                )),
                globalDelay = 1
        ))

        val responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(1, responseI1?.statusCode)
        assertEquals(2, responseI1?.delay)
    }

    @Test
    fun katch_intercept_delay_global_win() {
        val katchConfig = Config(
                arrayOf(Interceptor("i0", "/this/path",
                        arrayOf(Response("i0r1", 1)),
                        selectedResponseIndex = 0
                )),
                globalDelay = 1
        )
        val katch = RealKatch(katchConfig)

        val responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(1, responseI1?.statusCode)
        assertEquals(1, responseI1?.delay)
        // check response delay was not really updated but just used a copy of the response
        assertEquals(0, katchConfig.interceptors[0].responses[0].delay)
    }

    /**
     * Intercepted by the first one and no response returned so this Request is not mocked
     */
    @Test
    fun katch_intercept_selectedResponseIndex_same_interceptors_but_match_first_and_no_response_returned() {
        val katch = RealKatch(Config(arrayOf(
                Interceptor("i1", "/this/path",
                        arrayOf(
                                Response("i1r1", 101),
                                Response("i1r2", 102)
                        ),
                        Verb.GET
                ),
                Interceptor("i2", "/this/path",
                        arrayOf(
                                Response("i2r1", 201),
                                Response("i2r2", 202)
                        ),
                        Verb.GET,
                        selectedResponseIndex = 0
                )
        )))

        val responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        assertNull(responseI1)
    }

    @Test
    fun katch_intercept_selectedResponseIndex_pick_second_response_from_matched_verb_same_path() {
        val katch = RealKatch(Config(arrayOf(
                Interceptor("i0", "/this/path",
                        arrayOf(
                                Response("i0r1", 1),
                                Response("i0r2", 2)
                        ),
                        Verb.POST,
                        selectedResponseIndex = 0
                ),
                Interceptor("i1", "/this/path",
                        arrayOf(
                                Response("i1r1", 101),
                                Response("i1r2", 102)
                        ),
                        Verb.GET,
                        selectedResponseIndex = 1
                )
        )))

        val responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(102, responseI1?.statusCode)
    }

    @Test
    fun katch_intercept_selectedResponseIndex_update_selected() {
        val katchConfig = Config(arrayOf(
                Interceptor("i0", "/this/path",
                        arrayOf(
                                Response("i0r1", 1),
                                Response("i0r2", 2)
                        ),
                        Verb.POST,
                        selectedResponseIndex = 0
                ),
                Interceptor("i1", "/this/path",
                        arrayOf(
                                Response("i1r1", 101),
                                Response("i1r2", 102)
                        ),
                        Verb.GET,
                        selectedResponseIndex = 1
                )
        ))
        val katch = RealKatch(katchConfig)

        var responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(102, responseI1?.statusCode)

        // update config
        katch.config?.interceptors?.get(1)?.selectedResponseIndex = 0

        responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(101, responseI1?.statusCode)
    }

    @Test
    fun katch_intercept_bodyProvider() {
        val bodyString = "body test value"
        val katchConfig = Config(
                arrayOf(Interceptor("i0", "/this/path",
                        arrayOf(Response("i0r1", 1, object : BodyProvider {
                            override fun getBodyString() = bodyString

                        })),
                        selectedResponseIndex = 0
                )))
        val katch = RealKatch(katchConfig)

        val responseI1 = katch.intercept(Request(defaultUrl, Verb.GET))

        responseI1.assertResponseNotNull()
        assertEquals(bodyString, responseI1?.bodyProvider?.getBodyString())
    }

    private fun Response?.assertResponseNotNull() {
        assertNotNull(this, "Response is Null but was expected a match.")
    }
}