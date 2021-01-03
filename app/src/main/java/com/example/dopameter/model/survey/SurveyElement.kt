package com.example.dopameter.model.survey

data class SurveyElement(
    val SurveyID : String,
    val Element : String,
    val PrimaryAttribute : String,
    val SecondaryAttribute : String?,
    val TertiaryAttribute : String?,
    val Payload : Any
) {

}