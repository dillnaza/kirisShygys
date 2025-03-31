package KirisShygys.service.impl;

import KirisShygys.entity.User;
import KirisShygys.exception.UnauthorizedException;
import KirisShygys.repository.UserRepository;
import KirisShygys.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseService {

    protected final UserRepository userRepository;
    protected final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    public BaseService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    protected User getAuthenticatedUser(String token) {
        if (token == null || token.isBlank()) {
            logger.error("Missing authentication token");
            throw new UnauthorizedException("Missing authentication token");
        }
        try {
            String email = jwtUtil.extractUsername(token);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("Invalid or expired authentication token"));
        } catch (Exception e) {
            logger.error("Invalid authentication token", e);
            throw new UnauthorizedException("Invalid authentication token");
        }
    }
}