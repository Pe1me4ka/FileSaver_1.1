package android.bignerdranch.filesaver

import android.Manifest
import android.annotation.SuppressLint
import android.bignerdranch.filesaver.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val DOCUMENT_PRIMARY_PATH = "/document/primary"
private const val STORAGE_PATH = "/storage/emulated/0/"
private const val REQUEST_CODE = 123
@SuppressLint("SdCardPath")

class MainActivity : AppCompatActivity() {
    private val adapter = MyAdapter()

    private lateinit var filePath: String
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts
        .RequestPermission()) { isGranted: Boolean ->
        if (isGranted)
            Log.i("Permission: ", "Granted")
        else {
            Log.i("Permission: ", "Denied")
            val myDialogFragment = MyDialogFragment()
            val manager = supportFragmentManager
            myDialogFragment.show(manager, "myDialog")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedFile: Uri? = data?.data
            val stringOfPath = selectedFile
                ?.path
                ?.split(':')
                ?: listOf()

            //  /document/primary: -> /storage/emulated/0/
            filePath = when (stringOfPath.first()) {
                DOCUMENT_PRIMARY_PATH -> STORAGE_PATH + stringOfPath.last()
                else -> stringOfPath.last()
            }

            adapter.addFile(filePath)
            noFilesTVVisibility()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        requestPermission()

        adapter.generateList()
        recyclerInit()
        dragToDeleteInit()

        addBTN.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Выберите файл"), REQUEST_CODE)
        }

        noFilesTVVisibility()
    }

    private fun recyclerInit() {
        fileListRV.layoutManager = LinearLayoutManager(this@MainActivity)
        fileListRV.adapter = adapter
    }

    private fun dragToDeleteInit() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position =  viewHolder.adapterPosition

                adapter.apply {
                    deleteFile(position)
                    noFilesTVVisibility()
                    notifyItemRemoved(position)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.fileListRV)
    }

    private fun noFilesTVVisibility() {
        if (adapter.isEmpty())
            noFilesTV.visibility = View.VISIBLE
        else
            noFilesTV.visibility = View.GONE
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest
                .permission
                .WRITE_EXTERNAL_STORAGE) -> {
                //Permission is granted
            }
            else -> {
                //Permission has not been asked yet
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}