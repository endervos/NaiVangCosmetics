package demoweb.demo.service;

import demoweb.demo.dao.AccountRepository;
import demoweb.demo.entity.Account;
import demoweb.demo.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Collection;

@Primary
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean existsByAccountId(String accountId) {
        return accountRepository.existsByAccountId(accountId);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại: " + username);
        }
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) roleToAuthorities(account.getRole());
        return new User(
                account.getUsername(),
                account.getPassword(),
                account.getActive(),
                true,
                true,
                true,
                authorities
        );
    }

    private Collection<? extends GrantedAuthority> roleToAuthorities(Role role) {
        if (role == null) {
            throw new UsernameNotFoundException("Không tìm thấy vai trò của tài khoản!");
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName());
        return Collections.singletonList(authority);
    }
}