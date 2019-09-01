package com.upc.monitoringwalkers.ui.register.presenter

import com.upc.monitoringwalkers.common.arePasswordsSame
import com.upc.monitoringwalkers.common.isEmailValid
import com.upc.monitoringwalkers.common.isPasswordValid
import com.upc.monitoringwalkers.firebase.authentication.FirebaseAuthenticationInterface
import com.upc.monitoringwalkers.firebase.database.FirebaseDatabaseInterface
import com.upc.monitoringwalkers.model.PatientEntity
import com.upc.monitoringwalkers.model.RegisterPatientModel
import com.upc.monitoringwalkers.ui.register.view.RegisterView
import javax.inject.Inject

class RegisterPresenterImpl @Inject constructor(
    private val databaseInterface: FirebaseDatabaseInterface,
    private val authenticationInterface: FirebaseAuthenticationInterface
) : RegisterPresenter {

    private lateinit var view: RegisterView

    private val patientModel = RegisterPatientModel()

    override fun setView(view: RegisterView) {
        this.view = view
    }

    override fun onEmailChanged(email: String) {
        patientModel.email = email
        if (!isEmailValid(email)) {
            view.showEmailError()
        }
    }

    override fun onPasswordChanged(password: String) {
        patientModel.password = password
        if (!isPasswordValid(password)) {
            view.showPasswordError()
        }
    }

    override fun onRepeatPasswordChanged(repeatPassword: String) {
        patientModel.repeatPassword = repeatPassword
        if (!arePasswordsSame(patientModel.password, repeatPassword)) {
            view.showPasswordMatchingError()
        }
    }

    override fun onNameChanged(name: String) {
        patientModel.name = name
    }

    override fun onLastNameChanged(lastName: String) {
        patientModel.lastName = lastName
    }

    override fun onTreatmentChanged(treatment: String) {
        patientModel.treatment = treatment
    }

    override fun onAgeChanged(age: String) {
        patientModel.age = age.toInt()
    }

    override fun onRegisterClicked() {
        if (patientModel.isValid()) {
            authenticationInterface.register(
                patientModel.email,
                patientModel.password,
                patientModel.name
            ) { isSuccessful ->
                onRegisterResult(isSuccessful, patientModel)
            }
        }
    }

    private fun onRegisterResult(isSuccessful: Boolean, patient: RegisterPatientModel) {
        if (isSuccessful) {
            createPatient(patient)
            view.onRegisterSuccess()
        } else {
            view.showSignUpError()
        }
    }

    private fun createPatient(patient: RegisterPatientModel) {
        val id = authenticationInterface.getUserId()
        val patientEntity = PatientEntity(
            id,
            patient.name,
            patient.lastName,
            patient.email,
            patient.type,
            patient.age,
            patient.treatment
        )
        databaseInterface.createPatient(patientEntity)
    }
}