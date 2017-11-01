package edu.kit.ss17.chatsys.team1.client.Storage;

import edu.kit.ss17.chatsys.team1.client.Model.Roster;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.*;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Default Storage implementation.
 */
public class Storage implements StorageInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private static Storage        instance;
	private        SessionFactory sessionFactory;

	private Storage() {
		logger.info("initializing storage");
		this.sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		logger.info("storage initialized");
	}

	public static Storage getInstance() {
		return instance != null ? instance : (instance = new Storage());
	}

	public void truncateTable(String table) {
		Transaction tx = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.createQuery("delete from " + table).executeUpdate();
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}


	@Override
	public Collection<String> getActivePluginSets() {
		Transaction tx               = null;
		List        pluginsetsResult = new ArrayList<Map>();
		List        result           = new ArrayList<>();

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			pluginsetsResult = session.createQuery("SELECT p FROM PluginSet p WHERE p.enabled = TRUE").list();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}

		if (pluginsetsResult.size() > 0) {
			for (Object entry : pluginsetsResult) {
				Map entryMap = (HashMap) entry;
				result.add(entryMap.get("name"));
			}
		}

		return result;
	}

	@Override
	public void setActivePluginsSets(Collection<PluginSetInterface> pluginsSets) {
		truncateTable("PluginSet");
		Transaction tx = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();

			for (PluginSetInterface psi : pluginsSets) {
				Map pluginset = new HashMap();
				pluginset.put("name", psi.getName());
				pluginset.put("enabled", true);

				session.save("PluginSet", pluginset);
			}
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void saveLanguage(String lang) {
		Transaction tx = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();

			Map variable = new HashMap();
			variable.put("name", "language");
			variable.put("value", lang);

			session.saveOrUpdate("Variable", variable);
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public String getLanguage() {
		Transaction tx     = null;
		String      result = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			HashMap<String, String> queryResult = (HashMap) session.createQuery("SELECT v FROM Variable v WHERE v.name = 'language'").uniqueResult();
			tx.commit();

			if (queryResult != null)
				result = queryResult.get("value");
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void saveRoster(RosterInterface roster) {
		Transaction tx = null;

		if (roster.getAccount() != null)
			this.saveAccount(roster.getAccount());

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();

			session.saveOrUpdate(roster);
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void removeRoster(RosterInterface roster) {
		Transaction tx = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();

			session.delete(roster);
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public RosterInterface getRoster(AccountInterface account) {
		Transaction     tx     = null;
		RosterInterface result = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();

			result = (Roster) session.createQuery("FROM Roster WHERE account.jid.domainPart = :domain and account.jid.localPart = :local")
			                         .setParameter("domain", account.getJid().getDomainPart())
			                         .setParameter("local", account.getJid().getLocalPart())
			                         .uniqueResult();
			tx.commit();

			if (result != null) {
				for (ContactInterface contact : result.getContacts())
					contact.makePersistent();
			}

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void saveAccountConfiguration(AccountConfigurationInterface account) {
		Transaction tx = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.saveOrUpdate(account);
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void removeAccountConfiguration(AccountConfigurationInterface account) {
		Transaction tx = null;

		// First remove potential roster
		RosterInterface roster = this.getRoster(account.getAccount());
		if (roster != null)
			this.removeRoster(roster);

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.delete(account);
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public Collection<AccountConfigurationInterface> getAccountConfigurations() {
		Transaction tx      = null;
		List        configs = new ArrayList<>();

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			configs = session.createQuery("FROM AccountConfiguration").list();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}

		return configs;
	}

	@Override
	public void saveAccount(AccountInterface account) {
		Transaction tx = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.saveOrUpdate(account);
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void removeAccount(AccountInterface account) {
		Transaction tx = null;

		// First remove potential roster
		RosterInterface roster = this.getRoster(account);
		if (roster != null)
			this.removeRoster(roster);

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.delete(account);
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public AccountInterface getAccount(JID jid) {
		Transaction tx     = null;
		Account     result = null;

		try (Session session = this.sessionFactory.openSession()) {
			tx = session.beginTransaction();

			result = (Account) session.createQuery("FROM Account WHERE jid.localPart = :local and jid.domainPart = :domain")
			                          .setParameter("local", jid.getLocalPart())
			                          .setParameter("domain", jid.getDomainPart())
			                          .uniqueResult();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}

		return result;
	}
}
