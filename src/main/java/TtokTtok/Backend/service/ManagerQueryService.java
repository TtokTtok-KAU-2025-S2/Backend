package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.user.User;

import java.util.List;

public interface ManagerQueryService {

    List<KnockRequest> getReportedKnocks(User manager);
}