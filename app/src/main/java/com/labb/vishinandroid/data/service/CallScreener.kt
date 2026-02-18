package com.labb.vishinandroid.data.service

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.labb.vishinandroid.repositories.CallStateRepository

/**
 * Klassen använder sig av CallScreeningService för att kunna se inkommande samtal och telefonnummer
 * innan de visas till användaren.
 */
class CallScreener : CallScreeningService() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onScreenCall(callDetails: Call.Details) {

        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        if(isIncoming){
            // Hämtar objektet (En sträng som innehåller telefonnummer)
            val incomingNumber = callDetails.handle?.schemeSpecificPart
            Log.d("VishinGuard","Nummer från callDetails: $incomingNumber")
            // Okänt samtal hämtas på dessa olika sätt från API:n
            val isHidden = incomingNumber.isNullOrBlank() ||
                    incomingNumber.equals("anonymous",true) ||
                    incomingNumber.equals("private",true) ||
                    incomingNumber.equals("unknown",true)

            if(isHidden) {
                Log.d(
                    "VishinGuard", "Kommande samtal är från okänt nummer, intervention overlay" +
                            "kommer att visas när man öppnar känslig app"
                )
                CallStateRepository.setCallUnknown(true)
            }
            else{
                Log.d("VishinGuard","Kommande nummer är ej okänt")
                CallStateRepository.setCallUnknown(false)
            }
        }

        respondToCall(callDetails,CallResponse.Builder().build())
    }

}