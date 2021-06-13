.. _contest_terms:

.. include:: /migration/deprecation.inc

#####################################
Security Contest Terms and Conditions
#####################################

.. contents::
  :local:
  :backlinks: none
  :depth: 2

.. include:: contest-warning.txt

.. Note::
   :class: warning

   This has been reformatted from the original, and the enumeration
   list numbering style differs from the original document.

NO PURCHASE NECESSARY TO ENTER OR WIN. VOID WHERE PROHIBITED. CONTEST
IS OPEN TO RESIDENTS OF THE 50 UNITED STATES, THE DISTRICT OF COLUMBIA
AND WORLDWIDE, EXCEPT FOR ITALY, BRAZIL, QUEBEC, CUBA, IRAN, SYRIA,
NORTH KOREA, SUDAN AND MYANMAR.

ENTRY IN THIS CONTEST CONSTITUTES YOUR ACCEPTANCE OF THESE TERMS AND
CONDITIONS.

I. Binding Agreement

   In order to enter the Native Client Security Contest ("Contest"),
   you must agree to these Terms and Conditions ("Terms"). Therefore,
   please read these Terms prior to entry to ensure you understand and
   agree. You agree that submission of an entry in the Contest
   constitutes your agreement to these Terms. After reading the Terms
   and in order to participate, each Participant (as defined below)
   must complete the registration form, clicking the "I understand and
   agree" box (or the equivalent), on the Contest entry webpage. Once
   the Participant clicks the "I understand and agree" box (or the
   equivalent), the Terms form a binding legal agreement between each
   Participant and Google with respect to the Contest.

   Participants may not submit an Exploit, Issue or Summary to the
   Contest and are not eligible to receive the prizes described in
   these Terms unless they agree to these Terms. If a Participant is
   part of a team, each member of the team must read and agree to
   these Terms and click on the "I understand and agree" box (or the
   equivalent) described herein. Failure of any member of a team to
   agree to these Terms and click on the "I understand and agree" box
   (or the equivalent) described herein will disqualify the entire
   team.

   By entering, Participant warrants that Participant has not violated
   any employment agreement or other restriction imposed by their
   employer by participating in this Contest.

#. Description

   The Contest is organized by Google and is designed to motivate the
   developer community to identify and report security Exploits (as
   defined below) on Google’s Native Client software and reward those
   developers who identify one or more security Exploits that are
   evaluated as a winning exploit by the Judges.

   Once a Participant has registered for the Contest, the Participant
   will be asked to identify security Exploits in Google’s Native
   Client Software and enter those Exploits on Google’s `Native Client
   Issue Tracker <http://code.google.com/p/nativeclient/issues/list>`_
   website using the "Security Contest Template." At this point, the
   Exploit will become an Issue and will no longer be able to be
   identified by another Participant. Google will then verify that the
   Issue is reproducible. If so, that Issue will become a Verified
   Issue. Finally, the Participant will submit a Summary of up to their
   top ten best Issues that were submitted on the `Native
   Client Issue Tracker
   <http://code.google.com/p/nativeclient/issues/list>`_. Since it is
   possible that an Issue may not be verified until after the Contest
   End Date, if a Participant includes such an Issue in their Summary
   and such Issue is not ultimately verified, then that Issue will not
   be considered to be part of the Summary.

   Prizes will be awarded to those Participants who submit the best
   Summaries as determined in the sole discretion of the Judges when
   considering the Judging Criteria described herein.

#. Sponsor

   The Contest is sponsored by Google Inc. ("Google" or "Sponsor"), a
   Delaware corporation with its principal place of business at 1600
   Amphitheater Parkway, Mountain View, CA, 94043, USA.

#. Term

   The Contest begins at 9:00:00 A.M. Pacific Time (PT) Zone in the
   United States on Februrary 25th, 2009 ("Contest Start Date") and
   ends at 11:59:59 P.M. PT on May 5th, 2009 ("Contest End
   Date"). Participants must register by May 5th, 2009 at 11:59:59
   Pacific Time to be eligible to participate. ENTRANTS ARE
   RESPONSIBLE FOR DETERMINING THE CORRESPONDING TIME ZONE IN THEIR
   RESPECTIVE JURISDICTIONS.

#. Definitions

   Throughout these Terms, Google will use the following defined terms
   and words. Please review them carefully to ensure you understand.

   1. Covert Channel Attack: A "Covert Channel Attack" means an
      attempt to manipulate certain properties of a communications
      medium in an unexpected, unconventional, or unforeseen way in
      order to transmit information through the medium without
      detection by anyone other than the entities operating the covert
      channel. Exploits that are Covert Channel Attacks are excluded
      from the Contest.

   #. Exploit: An "Exploit" means a sequence of steps that require and
      use Native Client to produce or have the potential to produce
      behavior prohibited by Native Client's security policies and
      design which can be found at
      http://src.monyhar.org/viewvc/native_client/trunk/src/native_client/README.html.
      Google reserves the right to modify the security policies and
      design at any time. An example of an Exploit would be producing
      file system or network access outside of the scope of
      permissible use via JavaScript in a browser. An Exploit that
      defeats one but not all Native Client security measures is still
      considered to produce behavior prohibited by Native Client's
      security policies for the purposes of this Contest and would be
      entitled to be identified as an Exploit in the Contest.

   #. Inner Sandbox: The "Inner Sandbox" means the Native Client
      security system that a) inspects executables before running them
      to try to detect the potential for an executable to produce
      prohibited behavior, and b) prevents from running any
      executables that are detected to have the potential to produce
      prohibited behavior.

   #. Issue: An "Issue" means an entry of a single Exploit by a
      Participant into the `Native Client Issue Tracker
      <http://code.google.com/p/nativeclient/issues/list>`_ using a
      properly filled out Security Contest Template. Once the Exploit
      has been properly entered it becomes an Issue.

   #. Native Client Issue Tracker: The "Native Client Issue Tracker"
      is located at
      http://code.google.com/p/nativeclient/issues/list. It is a web
      application that manages and maintains a list of Issues,
      including Issues that are not eligible for contest entry.

   #. Native Client Version Number: The "Native Client Version Number"
      is defined as the number between the platform name (separated by
      an '_') and the file extension (separated by a '.') in the
      Native Client download. For example, if the the filename of the
      download on the Native Client download page is
      "nacl_linux_0.1_32_2009_01_16.tgz" or
      "nacl_windows_0.1_32_2009_01_16.zip", the Version Number is
      "0.1_32_2009_01_16".

   #. Outer Sandbox: The "Outer Sandbox" means the Native Client
      security system that 1) observes executables while they are
      running to detect the attempts at prohibited behavior and 2)
      terminates misbehaving executables if it observes any attempts
      to produce prohibited behavior.

   #. Participant: A "Participant" means any individual or team of
      individuals that has agreed to these Terms, meets the
      eligibility criteria described below, and is participating in
      the Contest.

   #. Side Channel Attack: A "Side Channel Attack" means any attack
      based on information gained as a side-effect of the
      implementation of a cryptosystem, rather than brute force or
      theoretical weaknesses in the algorithms. For example, attacks
      that use timing information, power consumption variation,
      electromagnetic leaks or sound to obtain information illicitly
      are side channel attacks. Exploits that are Side Channel Attacks
      are excluded from the Contest.

   #. Summary: A "Summary" means the final electronic document
      complying with the requirements of Section X that each
      Participant must submit in order to participate in the
      Contest. A Summary may contain up to 10 Issues. If Issues do not
      ultimately become Verified Issues, they will not be considered
      as part of the Summary and Participant understands and accepts
      the risk that if the Participant identified an Issue on a
      Summary that had not yet been verified, that Issue will not be
      considered as part of the Summary if not subsequently verified.

   #. Verified Issue: A "Verified Issue" means an Exploit that has
      been a) submitted to the `Native Client Issue Tracker
      <http://code.google.com/p/nativeclient/issues/list>`_ in
      accordance with these Terms, and b) confirmed by the Native
      Client team at Google to exhibit the behavior described in the
      Issue report.

#. Eligibility

   The Contest is open to Participants who (1) have agreed to these
   Terms; (2) who are of or above the legal age of majority, at the
   time of entry, to form valid contracts in their respective country,
   province or state of legal residence (and at least the age of 20 in
   Taiwan); (3) are not residents of Italy, Brazil, Quebec, Cuba,
   Iran, Syria, North Korea, Sudan, or Myanmar; and (4) who have
   software development experience. Sponsor reserves the right to
   verify eligibility and to adjudicate on any dispute at any
   time. The Contest is void in, and not open to residents of, Italy,
   Brazil, Quebec, Cuba, Iran, Syria, North Korea, Sudan, Myanmar, or
   to individuals and entities restricted by U.S. export controls and
   sanctions, and is void in any other nation, state, or province
   where prohibited or restricted by U.S. or local law.

   Employees and contractors of Google, affiliates and subsidiaries of
   Google, the Judges and members of their immediate families (defined
   as parents, children, siblings and spouse, regardless of where they
   reside and/or those living in the same household of each) are not
   eligible to participate in the Contest. Judges may not help any
   Participant with their submissions and Judges must recuse
   themselves in cases where they have a conflict of interest that
   becomes known to the Judge.

#. Registration & Entry Process

   1. All Participants must register at
      code.google.com/contests/nativeclient-security/ by May 5th, 2009
      at 11:59:59 Pacific Time. All individuals participating in the
      Contest (either as an individual Participant or as a member of a
      team) must provide the following registration information:

      (a) Email Address(es) of the Participant. The first member of a
          team to register must list the email addresses of all
          members of the Participant team, and all members must
          ultimately agree to the Terms as described more fully below.

      (#) Nationality and primary place of residence of the Participant.

      (#) If the Participant is a team, the email address of the team
          member who is selected to be the recipient of the prize. The
          first member of the team to register will designate this
          information in the initial team registration.

      (#) Participant name, which is the team name in the case of a
          team or the user name chosen by an individual in the case of
          an individual Participant.

      Failure to fully, completely and accurately provide this
      information will disqualify the Entry.

   #. Any potential prize recipient may be required to show proof of
      being the authorized account holder for an email address. The
      "Authorized Account Holder" is the natural person assigned to an
      email address by the relevant provider of email services.

   #. Participants that are teams must provide the above registration
      information for every individual who is a member of the
      team. Every individual who is part of the team must agree to the
      Terms in order for the team to be eligible to participate by
      clicking the "I understand and agree" box (or the equivalent) on
      the Contest entry webpage. Members of a team will be able to
      edit the information relating to the team only until the last
      member of the team has accepted these Terms by clicking the "I
      understand and agree" box (or the equivalent) on the Contest
      entry webpage. Issues submitted by members of a team prior to
      the time that all individual members of the team have clicked
      the "I understand and agree" box (or the equivalent) will not be
      valid Issue submissions and will not be eligible entries in the
      Contest. Google will send an email to all members of the team
      when the final team member has accepted the terms, however
      Google will have no liability for failure to send such an email
      or for the failure of any team member to receive the email.

   #. Issues submitted by Participants who are individuals prior to
      the time that the individual has clicked the "I understand and
      agree" box (or the equivalent) will not be valid Issue
      submissions and will not be eligible entries in the
      Contest. Google will send an email to the individual when the
      individual has accepted the terms, however Google will have no
      liability for failure to send such an email or for the failure
      of any team member to receive the email.

   #. All entries become the property of Sponsor and will not be
      acknowledged or returned. Entries are void if they are in whole
      or part illegible, incomplete, damaged, altered, counterfeit,
      obtained through fraud, or late.

   #. LIMIT ONE ENTRY PER PERSON. Individuals may only enter one time,
      whether as an individual Participant or as a team
      Participant. Google, in its sole discretion, may disqualify any
      Participant (including team Participants) that it believes has
      violated this provision.

#. Submission Process

   1. Each Participant must submit:

      (a) At least one Issue in the `Native Client Issue Tracker
          <http://code.google.com/p/nativeclient/issues/list>`_ that
          describes an Exploit and includes the information detailed
          in the "Issues" section below. Any team member can submit an
          Issue on behalf of the team. All entries will be deemed made
          by the Authorized Account Holder of the email address
          submitted at the time of entry.

      (#) One Summary per Participant that includes the information
          detailed in the "Summary" section below. Participant will be
          entitled to amend its Summary until the Contest End Date and
          only the last version will be considered by the Judges.

   #. Each Issue must be written in the English language. Google or
      the Judges may refuse to review submissions that they deem
      incomprehensible, include Issues that are not repeatable as
      determined by Google, or that otherwise do not meet the
      requirements of these Terms.

   #. To enter an Issue in the `Native Client Issue Tracker
      <http://code.google.com/p/nativeclient/issues/list>`_, each
      Participant must use the "Security Contest Template" and provide
      completely and accurately all information requested by the
      template. Any Issues that are not entered with the "Security
      Contest Template" may not be considered by the Judges. Each
      Issue must contain the items described in the "Issues" section
      of these Terms.

#. Issues

   1. Minimum requirements for Issues: Participant must identify an
      Exploit and enter the Exploit into the `Native Client Issue
      Tracker
      <http://code.google.com/p/nativeclient/issues/list>`_. Once the
      Exploit is submitted it becomes an Issue. Each submitted Issue
      must include (i) the following information and (ii) all
      additional information requested on the "Security Contest
      Template":

      (a) The user name (in the case of Individual Participants) or
          the team name (in the case of team Participants) of the
          Participant submitting the Issue, which must be identical to
          the user name or team name submitted during the registration
          process.

      (#) A gzipped tar archive (with paths relative to
          nacl/googleclient/native_client/tests/) that contains any
          instructions and files necessary to reproduce the Exploit,
          which must include:

          (1) A README.txt file that describes:

              * The version number of current version of Native Client
                at the time of submission. Issues submitted with a
                version number listed other than the current version
                at the time of submission will be invalid;

              * The steps required to reproduce the Exploit;

              * The effect of the Exploit; and

              * Platform requirements for the Exploit, including but
                not necessarily limited to:

              * browser version;

              * operating system name(s) and version(s); and/or

              * any other platform requirements relevant to the Exploit.

          (#) If the Exploit requires a binary executable, both the
              source code and binary executable must be provided upon
              creation of the Issue. Any subsequent updates to the
              source code or binary executable after the creation of
              the Issue will not be considered for the purposes of
              this Contest. The binary executable must build cleanly
              by executing the command "make" in the exploit directory
              (e.g. nacl/googleclient/native_client/tests/exploit1).

   #. Verified Issues: In order for an Issue to become a Verified
      Issue, Google will first examine the submitted Issue to
      determine whether it complies with the following:

      (a) The Exploit must not contain or depend upon access or use of
          any third party software or code that Google does not have
          readily available to it or that would require complying with
          third party license agreement that Google in its sole
          discretion deems onerous or burdensome.

      (#) Google must be able to replicate the Exploit in its sole
          discretion.

      (#) The Exploit must affect at least one "opt-" platform from a
          standard build of the most recent released version of Native
          Client as of the time of submission of the Issue for the
          Exploit.

   #. Timeliness

      (a) If the vulnerability exposed by the submitted Exploit was
          disclosed in a previously reported Issue (whether or not
          submitted by a Participant) or in the previously published
          Native Client release notes, the submission will be invalid
          for the purposes of this Contest. Two Exploits are
          considered to expose the same vulnerability if the
          theoretical patch required to fix one vulnerability also
          fixes the second vulnerability.

      (#) Google will update the Native Client source code base at
          most twice per week. These updates, if they occur, will
          appear Mondays and Thursdays between 3 p.m. and 8
          p.m. Pacific Time.

      (#) Issues will not be valid if they have been entered before
          the later of (i) the Contest Start Date or (ii) the time at
          which all members of a team Participant or the individual
          Participant, as the case may be, have accepted these Terms.

   #. Excluded Exploits. The following types of Exploits are invalid
      for the purposes of this Contest:

      * Covert Channel Attacks;

      * Sidechannel Attacks;

      * Exploits requiring a virtualized CPU;

      * Exploits that rely on features, misfeatures or defects of
        virtual machines (i.e. VMWare, Xen, Parallels etc.);

      * Exploits that require the machine to be previously compromised
        by malicious software (including but not limited to viruses or
        malware); and

      * Exploits that rely on hardware failures, other than Exploits
        which, in Google’s sole judgment, depend on CPU errata but
        which can be reproduced reliably with a common system
        configuration and under normal operating conditions, or
        statistically improbable hardware behaviors. Examples include
        but are not limited to Exploits that rely on memory errors
        induced by cosmic radiation, and Exploits that require
        abnormal heating, cooling or other abnormal physical
        conditions.

   #. Completeness. Issues submitted that lack any of the above
      materials or fail to meet any of the above criteria, may not be
      considered in the judging process at Google's sole
      discretion. Issues that are not included in a Participant
      Summary (see section below) will not be considered.

#. Summary

   1. Every Participant must submit a Summary at the `Native Client
      Issue Tracker
      <http://code.google.com/p/nativeclient/issues/list>`_ complying
      with the requirements of this section. The Participant must
      select no more than 10 of the Verified Issues submitted by the
      Participant for inclusion on the Summary. Each Summary must be
      in English and must contain the following information:

      * The Issues must be listed in descending order of severity, as
        determined by the Participant in accordance with the Judging
        Criteria.
       
      * Each Issue listed in the Summary must be identified by ID
        number of the Issue. The ID number is the identifying number
        created for each Issue as listed on the `Native Client Issue
        Tracker <http://code.google.com/p/nativeclient/issues/list>`_.

      * A description of the effect of each Exploit.

      * The platform requirements of each Exploit.

      * The version number(s) of Native Client software affected by
        each Exploit (which must be the version number of the Native
        Client software current at the time the Issue was submitted to
        the `Native Client Issue Tracker
        <http://code.google.com/p/nativeclient/issues/list>`_).

      * Any other details about the Exploit and the submission that
        are relevant to the judging criteria, such as, for example,
        the approach used in finding the exploits, innovative or
        scalable techniques used to discover exploits, or
        architectural analysis.

      * The team name or user name of the Participant. Google may, in
        its sole discretion, eliminate or disqualify any Summary that
        lists user names or team names that are not identical to the
        user name or team name of the Participant listed on the
        Contest entry form.

   #. Each Summary must be a maximum of 8 pages long, in PDF format
      viewable with Adobe Reader version 9. The Summary must be
      formatted for 8.5 inches x11 inches or A4 paper, with a minimum
      font size of 10 pt. Any submission that does not meet these
      formatting criteria may be disqualified at the sole discretion
      of Google.

   #. All Issues listed in the Summary will be verified by Google
      before submission of the Summary to the Judges after the Contest
      Closing Date. Participants may submit or resubmit their Summary
      at any time during the duration of the Contest, however, the
      Judges will consider only the last Summary from each Participant
      prior to the Contest Closing Date and ignore all other Summaries
      previously submitted by the Participant.

#. Judging

   1. After the Contest End Date and on or about May 15th, 2009, all
      submitted Summaries will be judged by one of at least three
      panels with a minimum of three experts in the field of online
      security ("Judges") on each panel. Judges will evaluate each
      Summary in accordance with the Judging Criteria described
      below. Each panel will evaluate a number of the submitted
      Summaries using the Judging Criteria described below and will
      select the highest ranking Summaries to move to the next level
      of judging. During the first round of judging, each panel will
      select no more than ten Summaries to move forward to the second
      round of judging unless there is a tie between or among any
      Participants. During the second round of judging, those
      Summaries selected during the first round of judging will then
      be evaluated by all Judges using the below Judging Criteria and
      the top five Summaries will be selected as potential
      winners. All decisions of the Judges are final and binding.

   #. Judging Criteria. The Judges will consider each Summary under
      following judging criteria ("Judging Criteria"):

      (a) Quality of Exploit. Quality will be decided by the Judges in
          their sole discretion and will be based on (in order of
          importance to the Judges) Severity, Scope, Reliability and
          Style.

          (i) Severity: the more disruptive the effects of the
              Exploit, the higher its quality. Here is a
              non-exhaustive ranking of the most common Exploits
              starting from 'minor' to 'severe':

              * Browser crash;

              * Denial of service or machine crash;

              * Compromise of the Outer Sandbox;

              * Information leak (such as of a cookie or password);

              * Compromise of both the Inner and Outer Sandbox; and/or

              * Prohibited side effect (such as reading or writing
                files to the client machine), escalation of privilege
                (such as executing other programs outside of Native
                Client).

              Any Exploit that does not address the above elements
              will be evaluated on a case-by-case basis and the
              severity of such Exploits will be determined solely at
              the Judge’s discretion.

          (#) Scope: the more computers that an Exploit would
              potentially affect, the bigger its scope and therefore
              higher the quality of the Exploit. Consider the
              following:

              * Exploits that affect all platforms supported by Native
                Client (where platform is defined as a browser,
                operating system and hardware combination) have higher
                quality than an Exploit specific to a particular
                platform.

              * Exploits that require non-current or beta versions
                (historic or future) of hardware or software are lower
                quality.

              * Exploits that rely on concurrent usage of other
                installed software or web content must make a
                compelling case about the likelihood of the
                prerequisite software or content being present, or
                they will be considered of lower quality.

          (#) Reliability: The more frequent or probable the
              occurrence identified by the Exploit, the more
              "reliable" it may be. Consider the following:

              * Exploits that require uncommon software to be
                installed on the machine in order to function will be
                deemed to have lower quality.

              * Entries that include Exploits that cannot be
                reproduced 100% of the time, but which can be
                reproduced a significant percentage of the time, will
                be deemed to have a lower quality to account for a
                lowered probability that the attack will succeed.

          (#) Style: Submissions that demonstrate exceptional style
              will receive a higher ranking. Factors that contribute
              to style include:

              * Ingenuity in mechanism used to bypass security;

              * Uniqueness of the Exploit;

              * Ingenuity in methods used to discover vulnerabilities;
                and/or Minimal size of Exploit to achieve the effect.

      (#) the Quantity of Exploits: Participants that submit more
          Exploits in their Summary (but no more than 10) may receive
          a higher ranking, weighted by quality. However, it is still
          possible that a Participant who submits one Exploit could
          still outweigh a Participant that submits several Exploits.

      Considering each of the factors described above, the Judges will
      give each Summary a "Score" from 1-10 that represents the Judges
      evaluation of the Summary. This "score" will determine which
      participants move from the first round of judging to the second
      round of judging, and which participants will be selected as a
      winner.

   #. Winner Selection

      Judges will review the Summaries as discussed in the "Judging"
      section, above. The Summaries with the five (5) highest scores
      will be selected as potentially winning Participants. In the
      event of a tie ranking for two or more Summaries, the
      Participant whose Summary had the highest ranking for "Severity"
      will receive the higher prize. In the event of a second tie, the
      Participant whose Summary had the highest ranking for "Scope"
      will receive the higher prize. Odds of winning depend on the
      number of eligible entries received and the skill of the
      Participants.

      The Judges are under no obligation to provide feedback on their
      decisions or on their judgment on specific Exploits they
      consider.

   #. Team Winners

      A special note about the prize distribution process for
      Participants who are entering as part of a team:

      A single member of each team shall be designated to receive the
      prize, if any, awarded to such team at the initial registration
      of the team, and Google shall have no responsibility for
      distribution of the prize among the team members.

      Each individual that enters as part of a team, understands and
      agrees that if their team is selected to receive a prize, the
      team is responsible for ensuring the funds are appropriately
      distributed to each member of the team. In addition, once a team
      has registered, the team may not add, remove, or substitute any
      members or otherwise change the composition of the team for the
      duration of the Contest. If any member of a team does not comply
      with these Terms, is ineligible or is disqualified, the team as
      a whole may be disqualified in Google’s sole discretion.

#. Prizes

   1. Information Required for Eligibility

      (a) On or about May 15th 2009 and upon selection of potential
          winners, Google will contact all winning Participants using
          the email addresses submitted at registration. In order to
          win the Contest and receive prizes, Participants, including
          each individual on a team, must provide additional
          information including:

          * first and last name;

          * address;

          * phone number; and

          * all other necessary information required by the US tax and
            legal authorities and /or the authorities of the countries
            they reside in.

      (#) All Participants will need to verify their identity with
          Google, before receiving their prize; however, Participants
          may provide an alias for use in any public documentation and
          marketing material issued publicly by Google, subject to
          limitations of the law and as required by law
          enforcement. Please be aware that in some jurisdictions, a
          list of winners must be made available and your name, and
          not the alias, will be provided on that list. If a
          Participant, or in the case of a team, any individual member
          of the team, refuses or fails to provide the necessary
          information to Google within 14 days of the Contest
          administrators' request for the required information, then
          Google may, in its sole discretion, disqualify the
          Participant's entry and select as an alternative potential
          winner the Participant with the next highest overall
          ranking. Google will not be held responsible for any failure
          of potential winners to receive notification that they are
          potential winners. Except where prohibited by law, each
          potential winner may be required to sign and return a
          Declaration of Eligibility, Liability & Publicity Release
          and Release of Rights and provide any additional information
          that may be required by Google. If required, potential
          winners must return all such required documents within 14
          calendar days following attempted notification or such
          potential winner will be deemed to have forfeited the prize
          and Google will select the Participant with the next highest
          overall ranking as the potential winner.

      (#) Prizes will be awarded within 6 months after the Contest End Date.

      (#) If fewer than 5 Participants or teams are found eligible,
          fewer than 5 winners will be selected.

      (#) Prizes are not transferable or substitutable, except by
          Google in its sole discretion in the event a prize becomes
          unavailable for any reason. In such an instance, Google will
          award a prize of equal or greater value.

      (#) LIMIT: Only one prize per Participant.

   #. Prize Amounts and Announcement

      Provided that the Participant has complied with these Terms,
      eligible Participants that are ranked in the top 5 positions of
      the competition by Judges will receive the following awards in
      U.S. Dollars based on their rank: 1st prize: $8,192.00, 2nd
      prize: $4,096.00, 3rd prize: $2,048.00, 4th prize: $1,024.00,
      5th prize: $1,024.00. Winning Entries will be announced on or
      about December 7th.

   #. Distribution of a Prize

      Google is not responsible for any division or distribution of
      the prizes among or between team members. Distribution or
      division of the prize among individual team members is the sole
      responsibility of the participating team. Google will award the
      prize only to the one (1) member of the team, who was identified
      by the Participant to receive the prize as part of the
      registration process. Google will attempt to reach only the
      designated recipient for purposes of distribution of the prize.

      Prizes are awarded without warranty of any kind from Google,
      express or implied, without limitation, except where this would
      be contrary to federal, state, provincial, or local laws or
      regulations. All federal, state, provincial and local laws and
      regulations apply.

   #. Taxes

      Payments to potential prize winners are subject to the express
      requirement that they submit to Google all documentation
      requested by Google to permit it to comply with all applicable
      US, state, local and foreign (including provincial) tax
      reporting and withholding requirements. All prizes will be net
      of any taxes Google is required by law to withhold. All taxes
      imposed on the prize are the sole responsibility of the prize
      recipient.

      In order to receive a prize, potential prize recipients must
      submit the tax documentation requested by Google or otherwise
      required by applicable law, to Google or the relevant tax
      authority, all as determined by applicable law, including, where
      relevant, the law of the potential prize recipient's country of
      residence. The potential prize recipient is responsible for
      ensuring that they comply with all the applicable tax laws
      and filing requirements. If a potential prize recipient fails to
      provide such documentation or comply with such laws, the prize
      may be forfeited and Google may, in its sole discretion, select
      an alternative potential prize recipient.

#. General Conditions

   1. Right to Disqualify. A Participant may be prohibited from
      participating in or be disqualified from this Contest if, in
      Google's sole discretion, it reasonably believes that the
      Participant or any member of a Participant team has attempted to
      undermine the legitimate operation of the Contest by cheating,
      deception, or other unfair playing practices or annoys, abuses,
      threatens or harasses any other Participants, Google, or the
      Judges. Google further reserves the right to disqualify any
      Issue that it believes in its sole and unfettered discretion
      infringes upon or violates the rights of any third party,
      otherwise does not comply with these Terms, or violates U.S. or
      applicable local law in Participant's country of residence.

      Google further reserves the right to disqualify any Participant
      who tampers with the submission process or any other part of the
      Contest. Any attempt by a Participant to deliberately damage any
      website or undermine the legitimate operation of the Contest is
      a violation of criminal and civil laws and should such an
      attempt be made, Google reserves the right to seek damages from
      any such Participant to the fullest extent of the applicable
      law.

   #. Internet Disclaimer. Google is not responsible for any
      malfunction of the entire Contest, the website displaying the
      Contest terms and entry information, or any late, lost, damaged,
      misdirected, incomplete, illegible, undeliverable, or destroyed
      Exploits, Issues or Summaries due to system errors, failed,
      incomplete or garbled computer or other telecommunication
      transmission malfunctions, hardware or software failures of any
      kind, lost or unavailable network connections, typographical or
      system/human errors and failures, technical malfunction(s) of
      any telephone network or lines, cable connections, satellite
      transmissions, servers or providers, or computer equipment,
      traffic congestion on the Internet or at the website displaying
      the Contest or any combination thereof, including other
      telecommunication, cable, digital or satellite malfunctions
      which may limit an entrant’s ability to participate. Google is
      not responsible for availability of the `Native Client Issue
      Tracker <http://code.google.com/p/nativeclient/issues/list>`_
      from your preferred point of Internet access. In the event of a
      technical disruption, Google may, in its sole discretion, extend
      the Contest End Date for a reasonable period. Google will
      attempt to notify Participants of any such extension by email at
      the email address in the registration information, but shall
      have no liability for any failure of such notification.

   #. Exploits Independently Discovered by Google. You acknowledge and
      understand that Google may discover Exploits independently that
      may be similar to or identical to your Issues in terms of
      function, vulnerability, or in other respects. You agree that
      you will not be entitled to any rights in, or compensation in
      connection with, any such similar or identical applications
      and/or ideas. You acknowledge that you have submitted your entry
      voluntarily and not in confidence or in trust.

   #. No Contract for Employment. You acknowledge that no
      confidential, fiduciary, agency or other relationship or
      implied-in-fact contract now exists between you and Google and
      that no such relationship is established by your submission of
      an entry to Google in this Contest. Under no circumstances shall
      the submission of an entry in the Contest, the awarding of a
      prize, or anything in these Terms be construed as an offer or
      contract of employment with Google.

   #. Intellectual Property Rights and License. Participants warrant
      that their Exploit and Summary are their own original work and,
      as such, they are the sole and exclusive owner and rights holder
      of the submitted Exploit and Summary and that they have the
      right to submit the Exploit and Summary in the Contest and grant
      all required licenses. Each Participant agrees not to submit any
      Exploit and Summary that (a) infringes any third party
      proprietary rights, intellectual property rights, industrial
      property rights, personal or moral rights or any other rights,
      including without limitation, copyright, trademark, patent,
      trade secret, privacy, publicity or confidentiality obligations;
      or (b) otherwise violates the applicable state, federal,
      provincial or local law.

      As between Google and the Participant, the Participant retains
      ownership of all intellectual and industrial property rights in
      and to the Issues and Summary that Participant created. As a
      condition of entry, Participant grants Google a perpetual,
      irrevocable, worldwide, royalty-free, and non-exclusive license
      to use, reproduce, publicly perform, publicly display,
      distribute, sublicense and create a derivative work from, any
      Issue or Summary that Participant submits to this Contest for
      the purposes of allowing Google to test, evaluate and fix or
      remedy the Issue and Summary for purposes of the Contest and
      modifying or improving the Native Client software or any other
      current or future Google product or service.

      Participant also grants Google the right to reproduce and
      distribute the Issue and the Summary. In addition, Participant
      specifically agrees that Google shall have the right to use,
      reproduce, publicly perform, and publicly display the Issue and
      Summary in connection with the advertising and promotion of the
      Native Client software or any other current or future Google
      product or service via communication to the public or other
      groups, including, but not limited to, the right to make
      screenshots, animations and video clips available for
      promotional purposes.

   #. Privacy. Participants agree that personal data provided to
      Google during the Contest, including name, mailing address,
      phone number, and email address may be processed, stored, and
      otherwise used for the purposes and within the context of the
      Contest. This data will be maintained in accordance with the
      Google Privacy Policy found at
      http://www.google.com/privacypolicy.html. This data will also be
      transferred into the United States. By entering, Participants
      agree to the transmission, processing, and storage of this
      personal data in the United States.

      Participants also understand this data may be used by Google in
      order to verify a Participant's identity, postal address and
      telephone number in the event a Participant qualifies for a
      prize. Participants have the right to access, review, rectify or
      cancel any personal data held by Google in connection with the
      Contest by writing to Google at the address listed below in the
      section entitled "Winner’s List."

      For residents of the European Union:

      Pursuant to EU law pertaining to data collection and processing,
      you are informed that:

      * The data controller is Google and the data recipients are
        Google and its agents;

      * Your data is collected for purposes of administration of the
        Native Client Security Contest;

      * You have a right of access to and withdrawal of your personal
        data. You also have a right of opposition to the data
        collection, under certain circumstances. To exercise such
        right, You may write to: Native Client Security Contest,
        Google Inc., 1600 Amphitheater Parkway, Mountain View, CA
        94043, USA.

      * Your personal data will be transferred to the U.S.

   #. Indemnity. To the maximum extent permitted by law, each
      Participant indemnifies and agrees to keep indemnified Google
      and Judges at all times from and against any liability, claims,
      demands, losses, damages, costs and expenses resulting from any
      act, default or omission of the Participant and/or a breach of
      any warranty set forth herein. To the maximum extent permitted
      by law, each Participant agrees to defend, indemnify and hold
      harmless Google, its affiliates and their respective directors,
      officers, employees and agents from and against any and all
      claims, actions, suits or proceedings, as well as any and all
      losses, liabilities, damages, costs and expenses (including
      reasonable attorneys fees) arising out of or accruing from:

      (a) any material uploaded or otherwise provided by the
          Participant that infringes any copyright, trademark, trade
          secret, trade dress, patent or other intellectual property
          right of any person or defames any person or violates their
          rights of publicity or privacy,

      (b) any misrepresentation made by the Participant in connection
          with the Contest;

      (c) any non-compliance by the Participant with these Terms; and

      (d) claims brought by persons or entities other than the parties
          to these Terms arising from or related to the Participant's
          involvement with the Contest.

      To the extent permitted by law, Participant agrees to hold
      Google, its respective directors, officers, employees and
      assigns harmless for any injury or damage caused or claimed to
      be caused by participation in the Contest and/or use or
      acceptance of any prize, except to the extent that any death or
      personal injury is caused by the negligence of Google.

   #. Elimination. Any false information provided within the context
      of the Contest by any Participant including information
      concerning identity, mailing address, telephone number, email
      address, or ownership of right, or non-compliance with these
      Terms or the like may result in the immediate elimination of the
      Participant from the Contest. In the event an individual who is
      a member of a team supplies information that is covered by this
      section, the entire team shall be disqualified.

   #. Right to Cancel. If for any reason the Contest is not capable of
      running as planned, including infection by computer virus, bugs,
      tampering, unauthorized intervention, fraud, technical failures,
      or any other causes which corrupt or affect the administration,
      security, fairness, integrity, or proper conduct of the Contest,
      Google reserves the right at its sole discretion to cancel,
      terminate, modify or suspend the Contest.

   #. Forum and Recourse to Judicial Procedures. These Terms shall be
      governed by, subject to, and construed in accordance with the
      laws of the State of California, United States of America,
      excluding all conflict of law rules. If any provision(s) of
      these Terms are held to be invalid or unenforceable, all
      remaining provisions hereof will remain in full force and
      effect. To the extent permitted by law, the rights to litigate,
      seek injunctive relief or make any other recourse to judicial or
      any other procedure in case of disputes or claims resulting from
      or in connection with this Contest are hereby excluded, and all
      Participants expressly waive any and all such rights.

   #. Arbitration. By entering the Contest, you agree that exclusive
      jurisdiction for any dispute, claim, or demand related in any
      way to the Contest will be decided by binding arbitration. All
      disputes between you and Google, of whatsoever kind or nature
      arising out of these Terms, shall be submitted to Judicial
      Arbitration and Mediation Services, Inc. ("JAMS") for binding
      arbitration under its rules then in effect in the San Jose,
      California, USA area, before one arbitrator to be mutually
      agreed upon by both parties. The parties agree to share equally
      in the arbitration costs incurred.

   #. Winner List

      You may request a list of winners after December 7th, 2009 by
      writing to:

      | Native Client Security Contest
      | Google Inc.
      | 1600 Amphitheater Parkway
      | Mountain View, CA 94043
      | USA

      (Residents of Vermont need not supply postage).
