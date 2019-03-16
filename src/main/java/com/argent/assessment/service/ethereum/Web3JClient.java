package com.argent.assessment.service.ethereum;


import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;
import org.web3j.protocol.parity.methods.response.ParityExportAccount;
import org.web3j.protocol.parity.methods.response.ParityTracesResponse;
import org.web3j.protocol.parity.methods.response.Trace;

import java.util.List;

public class Web3JClient {

    private static Web3jService service = new HttpService("http://localhost:8545");
    private static Web3j web3j = Web3j.build(service);
    private static Parity parity = Parity.build(service);

    public static List<Trace> getCallActionsInBlock(long blockNum) throws Exception {
        DefaultBlockParameterNumber number = new DefaultBlockParameterNumber(blockNum);
        Request<?, ParityTracesResponse> request = parity.traceBlock(number);
        ParityTracesResponse response = request.send();
        return response.getTraces();
    }

    public static List<Trace> getCallAction(String hash) throws Exception {
        Request<?, ParityTracesResponse> request = parity.traceTransaction(hash);
        ParityTracesResponse response = request.send();
        return response.getTraces();
    }

    public static WalletFile exportAccount(String address, String password) throws Exception {
        Request<?, ParityExportAccount> request = parity.parityExportAccount(address, password);
        ParityExportAccount response = request.send();
        return response.getWallet();
    }

    public Web3j getWeb3j() {
        return web3j;
    }

}
