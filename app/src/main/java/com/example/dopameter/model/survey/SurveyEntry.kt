package com.example.dopameter.model.survey

import com.google.type.DateTime

data class SurveyEntry(
    val SurveyID : String,
    val SurveyName : String,
    val SurveyDescription : String?,
    val SurveyOwnerID : String,
    val SurveyBrandID : String,
    val DivisionID : String?,
    val SurveyLanguage : String,
    val SurveyActiveResponseSet : String,
    val SurveyStatus : String,
    val SurveyStartDate : String,
    val SurveyExpirationDate : String,
    val SurveyCreationDate : String,
    val CreatorID : String,
    val LastModified : String,
    val LastAccessed : String,
    val LastActivated : String,
    val Deleted : String?
)