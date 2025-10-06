package com.evcharge.mobile.ui.owner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.evcharge.mobile.R
import com.evcharge.mobile.data.remote.OwnerApi
import com.evcharge.mobile.data.remote.OwnerUpdateRequest
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.util.Result
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    
    private lateinit var authSession: AuthSession
    private lateinit var ownerApi: OwnerApi
    private lateinit var progressBar: ProgressBar
    private lateinit var tvName: TextView
    private lateinit var tvNic: TextView
    private lateinit var tvRole: TextView
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDeactivate: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        authSession = AuthSession(TokenStore(requireContext()), com.evcharge.mobile.App.instance.dbHelper)
        
        val httpClient = com.evcharge.mobile.data.remote.HttpClient()
        httpClient.setAuthToken(authSession.getToken())
        ownerApi = OwnerApi(httpClient)
        
        progressBar = view.findViewById(R.id.progress_bar)
        tvName = view.findViewById(R.id.tv_name)
        tvNic = view.findViewById(R.id.tv_nic)
        tvRole = view.findViewById(R.id.tv_role)
        etName = view.findViewById(R.id.et_name)
        etEmail = view.findViewById(R.id.et_email)
        etPhone = view.findViewById(R.id.et_phone)
        btnSave = view.findViewById(R.id.btn_save)
        btnDeactivate = view.findViewById(R.id.btn_deactivate)
        
        setupViews()
        loadProfile()
    }
    
    private fun setupViews() {
        btnSave.setOnClickListener {
            saveProfile()
        }
        
        btnDeactivate.setOnClickListener {
            deactivateAccount()
        }
    }
    
    private fun loadProfile() {
        val user = authSession.getCurrentUser()
        if (user != null) {
            tvName.text = user.name
            tvNic.text = "NIC: ${user.nic}"
            tvRole.text = user.role
            etName.setText(user.name)
            etEmail.setText(user.email ?: "")
            etPhone.setText(user.phone ?: "")
        }
    }
    
    private fun saveProfile() {
        val name = etName.text?.toString()?.trim()
        val email = etEmail.text?.toString()?.trim()
        val phone = etPhone.text?.toString()?.trim()
        
        if (name.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        val nic = authSession.getNic()
        if (nic.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            val request = OwnerUpdateRequest(
                name = name,
                email = email.takeIf { !it.isNullOrEmpty() },
                phone = phone.takeIf { !it.isNullOrEmpty() }
            )
            
            val result = ownerApi.updateOwner(nic, request)
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    loadProfile()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Failed to update profile: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            showLoading(false)
        }
    }
    
    private fun deactivateAccount() {
        // TODO: Show confirmation dialog
        val nic = authSession.getNic()
        if (nic.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            val result = ownerApi.deactivateOwner(nic)
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Account deactivated", Toast.LENGTH_SHORT).show()
                    authSession.logout()
                    requireActivity().finish()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Failed to deactivate account: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            showLoading(false)
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
        btnDeactivate.isEnabled = !show
    }
}
