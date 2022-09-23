package com.vibs.maashakti

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vibs.maashakti.adapter.PassCardAdapter
import com.vibs.maashakti.adapter.PassCardListener
import com.vibs.maashakti.api.Status
import com.vibs.maashakti.api.models.ParticipantData
import com.vibs.maashakti.base.StartActivityCallback
import com.vibs.maashakti.databinding.FragmentFirstBinding
import com.vibs.weatherdemosdk.base.BaseFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment(R.layout.fragment_first) {

    private var deleteItem: ParticipantData? = null
    private lateinit var cardAdapter: PassCardAdapter
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mCallback: MainListener

    private val viewModel by lazy {
        ViewModelProvider(this)[CardViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        try {
            mCallback = activity as MainListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString() + " must implement HeadlineListener"
            )
        }
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        initUi()

        observer()

        getPlayerPass()

    }

    override fun initData() {
        super.initData()
    }

    override fun initUi() {
        super.initUi()
        binding.groupCreate.visibility = View.GONE
        binding.groupCardList.visibility = View.GONE

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                cardAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                cardAdapter.filter.filter(newText)
                return false
            }

        })

        binding.rvPass.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        cardAdapter = PassCardAdapter(object : PassCardListener {
            override fun onDelete(item: ParticipantData) {
                deleteItem = item
                openConfirmationDialogDialog(
                    18,
                    "Confirmation!",
                    "Are you sure want to delete this Player card!",
                    "Yes",
                    "NO",
                    false
                )
            }

            override fun onDetail(item: ParticipantData) {
                deleteItem = item

                openUserConfirmationDialogDialog(
                    87,
                    "Player Info!",
                    item,
                    "Okay",
                    "Delete",
                    false
                )
            }

        })
        binding.rvPass.adapter = cardAdapter
    }

    override fun observer() {
        super.observer()
        viewModel.cardList.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.groupCreate.visibility = View.VISIBLE
                binding.groupCardList.visibility = View.GONE
                return@observe
            }

            it.info?.let { list ->
                binding.groupCreate.visibility = View.GONE
                binding.groupCardList.visibility = View.VISIBLE
                cardAdapter.addData(list)
                cardAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDialogPositiveButtonClick(dialogTag: Int) {
        super.onDialogPositiveButtonClick(dialogTag)
        //Call Delete API call
        if (dialogTag == 18) {
            deleteItem?.let { item ->
                postRemoveCardData(item)
            }
        }
    }

    override fun onDialogNegativeButtonClick(dialogTag: Int) {
        super.onDialogNegativeButtonClick(dialogTag)

        if (dialogTag == 87) {
            deleteItem?.let {
                openDeleteConfirmationDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openDeleteConfirmationDialog() {
        openConfirmationDialogDialog(
            18,
            "Confirmation!",
            "Are you sure want to delete this Player card!",
            "Yes",
            "NO",
            false
        )
    }

    /**
     * Make API call to get verify details
     */
    private fun getPlayerPass() {

        if (!isNetworkConnected) {
            showNoInternetDialog()
            return
        }

        viewModel.getPassList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.SUCCESS -> {
                    hideProgressDialog()
                    viewModel.setCardListResponse(it.data)
                }
                Status.ERROR -> {
                    viewModel.setCardListResponse(null)
                    hideProgressDialog()
                }
            }
        }
    }

    /**
     * Make API call to get verify details
     */
    private fun postRemoveCardData(item: ParticipantData) {

        if (!isNetworkConnected) {
            showNoInternetDialog()
            return
        }

        //perform API call
        val map: MutableMap<String, RequestBody> = HashMap()

        map["user_nfc_no"] =
            (item.userNfcNo?:"").toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map["user_id"] =
            (item.userId?:"").toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map["secret_token"] =
            "mashakti@admin".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        viewModel.postRemoveCardData(map).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.SUCCESS -> {
                    hideProgressDialog()
                    binding.etSearch.setQuery("", false)
                    getPlayerPass()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message?:"", Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }

}