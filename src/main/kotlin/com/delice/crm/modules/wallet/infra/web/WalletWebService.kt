package com.delice.crm.modules.wallet.infra.web

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.modules.wallet.domain.entities.Wallet
import com.delice.crm.modules.wallet.domain.usecase.WalletUseCase
import com.delice.crm.modules.wallet.domain.usecase.response.WalletPaginationResponse
import com.delice.crm.modules.wallet.domain.usecase.response.WalletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/wallet")
class WalletWebService(
    private val walletUseCase: WalletUseCase
) {
    @PostMapping("/create")
    fun createWallet(
        @RequestBody wallet: Wallet,
        request: HttpServletRequest
    ): ResponseEntity<WalletResponse> {
        val user = getCurrentUser()

        val response = walletUseCase.createWallet(wallet, user.uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PutMapping("/update")
    fun updateWallet(
        @RequestBody wallet: Wallet,
        request: HttpServletRequest
    ): ResponseEntity<WalletResponse> {
        val user = getCurrentUser()

        val response = walletUseCase.updateWallet(wallet, user.uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getByUUID")
    fun getWalletByUUID(
        @RequestParam(
            value = "uuid",
            required = true
        ) walletUUID: UUID
    ): ResponseEntity<WalletResponse> {
        val response = walletUseCase.getWalletByUUID(walletUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getPagination")
    fun getWalletPagination(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        request: HttpServletRequest
    ): ResponseEntity<WalletPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = walletUseCase.getWalletPagination(count, page, params)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }
}