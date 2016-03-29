package org.elsysbg.ip.todo.services;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.elsysbg.ip.todo.entities.Member;

@Singleton
public class AuthenticationService {
	private final MembersService membersService;

	@Inject
	public AuthenticationService(MembersService membersService) {
		this.membersService = membersService;
	}

	public Member getCurrentlyLoggedInMember() {
		// TODO get currently logged member from security framework
		final List<Member> members = membersService.getMembers();
		return members.iterator().next();
		// or return members.get(0);
	}

	public String encryptPassword(String password) {
		final PasswordService passwordService = getPasswordService();
		return passwordService.encryptPassword(password);
	}

	private PasswordService getPasswordService() {
		final RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
		final Collection<Realm> realms = securityManager.getRealms();
		PasswordMatcher credentialsMatcher = null;
		for (Realm next : realms) {
			if (next instanceof AuthenticatingRealm) {
				final AuthenticatingRealm authenticatingRealm = (AuthenticatingRealm) next;
				if (authenticatingRealm.getCredentialsMatcher() instanceof PasswordMatcher) {
					credentialsMatcher = (PasswordMatcher) authenticatingRealm.getCredentialsMatcher();
					break;
				}
			}
		}
		if (credentialsMatcher == null) {
			throw new IllegalStateException("Bad configuration");
		}
		return credentialsMatcher.getPasswordService();
	}
}