package com.miguel.statscalculator.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.miguel.statscalculator.core.util.ClipboardUtil
import com.miguel.statscalculator.core.util.ExportUtil
import com.miguel.statscalculator.core.util.FileImportUtil

@Composable
fun InputActionBar(
    onTextPasted: (String) -> Unit,
    onFileLoaded: (String) -> Unit,
    reportToShare: String? = null,
    reportTitle: String = "Relatório StatsCalculator"
) {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileContent = FileImportUtil.readTextFromUri(context, it)
            if (fileContent.isNotBlank()) {
                onFileLoaded(fileContent)
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                val pasted = ClipboardUtil.pasteFromClipboard(context)
                if (pasted.isNotBlank()) onTextPasted(pasted)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Colar")
        }

        OutlinedButton(
            onClick = { filePickerLauncher.launch("*/*") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Arquivo")
        }

        if (reportToShare != null) {
            Button(
                onClick = { ExportUtil.shareTextReport(context, reportTitle, reportToShare) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Exportar")
            }
        }
    }
}