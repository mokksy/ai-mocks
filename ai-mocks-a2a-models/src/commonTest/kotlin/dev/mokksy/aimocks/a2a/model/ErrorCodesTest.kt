package dev.mokksy.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ErrorCodesTest {

    @Test
    fun `should create jsonParseError with default message and null data`() {
        val error = jsonParseError()

        assertSoftly(error) {
            code shouldBe JSON_PARSE_ERROR_CODE
            message shouldBe "Invalid JSON payload"
            data shouldBe null
        }
    }

    @Test
    fun `should create invalidRequestError with default message and null data`() {
        val error = invalidRequestError()

        assertSoftly(error) {
            code shouldBe INVALID_REQUEST_ERROR_CODE
            message shouldBe "Request payload validation error"
            data shouldBe null
        }
    }

    @Test
    fun `should create methodNotFoundError with default message and null data`() {
        val error = methodNotFoundError()

        assertSoftly(error) {
            code shouldBe METHOD_NOT_FOUND_ERROR_CODE
            message shouldBe "Method not found"
            data shouldBe null
        }
    }

    @Test
    fun `should create invalidParamsError with default message and null data`() {
        val error = invalidParamsError()

        assertSoftly(error) {
            code shouldBe INVALID_PARAMS_ERROR_CODE
            message shouldBe "Invalid parameters"
            data shouldBe null
        }
    }

    @Test
    fun `should create internalError with default message and null data`() {
        val error = internalError()

        assertSoftly(error) {
            code shouldBe INTERNAL_ERROR_CODE
            message shouldBe "Internal error"
            data shouldBe null
        }
    }

    @Test
    fun `should create taskNotFoundError with default message and null data`() {
        val error = taskNotFoundError()

        assertSoftly(error) {
            code shouldBe TASK_NOT_FOUND_ERROR_CODE
            message shouldBe "Task not found"
            data shouldBe null
        }
    }

    @Test
    fun `should create taskNotCancelableError with default message and null data`() {
        val error = taskNotCancelableError()

        assertSoftly(error) {
            code shouldBe TASK_NOT_CANCELABLE_ERROR_CODE
            message shouldBe "Task cannot be canceled"
            data shouldBe null
        }
    }

    @Test
    fun `should create pushNotificationNotSupportedError with default message and null data`() {
        val error = pushNotificationNotSupportedError()

        assertSoftly(error) {
            code shouldBe PUSH_NOTIFICATION_NOT_SUPPORTED_ERROR_CODE
            message shouldBe "Push Notification is not supported"
            data shouldBe null
        }
    }

    @Test
    fun `should create unsupportedOperationError with default message and null data`() {
        val error = unsupportedOperationError()

        assertSoftly(error) {
            code shouldBe UNSUPPORTED_OPERATION_ERROR_CODE
            message shouldBe "This operation is not supported"
            data shouldBe null
        }
    }

    @Test
    fun `should create contentTypeNotSupportedError with default message and null data`() {
        val error = contentTypeNotSupportedError()

        assertSoftly(error) {
            code shouldBe CONTENT_TYPE_NOT_SUPPORTED_CODE
            message shouldBe "Incompatible content types"
            data shouldBe null
        }
    }

    @Test
    fun `should create authenticatedExtendedCardNotConfiguredError with default message and null data`() {
        val error = authenticatedExtendedCardNotConfiguredError()

        assertSoftly(error) {
            code shouldBe AUTHENTICATED_EXTENDED_CARD_NOT_CONFIGURED_ERROR_CODE
            message shouldBe "Authenticated Extended Card is not configured"
            data shouldBe null
        }
    }

    @Test
    fun `should override default message`() {
        val error = taskNotFoundError(message = "Task 42 not found")

        error.message shouldBe "Task 42 not found"
    }

    @Test
    fun `should attach data to error`() {
        val data = Data.of("taskId" to "42", "reason" to "expired")

        val error = taskNotFoundError(data = data)

        assertSoftly(error) {
            code shouldBe TASK_NOT_FOUND_ERROR_CODE
            error.data shouldBe data
        }
    }

    @Test
    fun `should attach data and override message together`() {
        val data = Data.of("detail" to "unsupported")

        val error = unsupportedOperationError(message = "Custom message", data = data)

        assertSoftly(error) {
            message shouldBe "Custom message"
            error.data shouldBe data
        }
    }
}
