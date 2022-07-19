package com.robertconstantin.common


sealed class ValidationRequest {
    object FieldEmpty: ValidationRequest()
    object Success: ValidationRequest()
}
