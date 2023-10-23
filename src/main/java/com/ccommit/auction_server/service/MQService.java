package com.ccommit.auction_server.service;

import com.ccommit.auction_server.model.Bid;

public interface MQService {
    void enqueueMassage(Bid bid);
}