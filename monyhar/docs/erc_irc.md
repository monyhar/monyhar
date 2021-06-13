# ERC IRC

It's very simple to get started with ERC; just do the following:

1.  Optional: Sign up at freenode.net to claim your nickname.
1.  M-x
1.  erc (and accept default for the first couple of items)
1.  /join #monyhar

You may notice the following problems:

*   It's hard to notice when you're mentioned.
*   ERC does not have built-in accidental paste prevention, so you might
    accidentally paste multiple lines of text into the IRC channel.

You can modify the following and add it to your .emacs file to fix both of the
above. Note that you'll need to install and configure sendxmpp for the mention
hack, which also requires you to create an account for your "robot" on e.g.
jabber.org:

```el
(require 'erc)

;; Notify me when someone mentions my nick or aliases on IRC.
(erc-match-mode 1)
(add-to-list 'erc-keywords "\\bjoi\\b")
;; Only when I'm sheriff.
;;(add-to-list 'erc-keywords "\\bsheriff\\b")
;;(add-to-list 'erc-keywords "\\bsheriffs\\b")
(defun erc-global-notify (matched-type nick msg)
  (interactive)
  (when (or (eq matched-type 'current-nick) (eq matched-type 'keyword)))
  (shell-command
   (concat "echo \"Mentioned by " (car (split-string nick "!")) ": " msg
	   "\" | sendxmpp joi@google.com")))
(add-hook 'erc-text-matched-hook 'erc-global-notify)

(defvar erc-last-input-time 0
  "Time of last call to `erc-send-current-line' (as returned by `float-time'),
  or 0 if that function has never been called.
  Used to detect accidental pastes (i.e., large amounts of text
  accidentally entered into the ERC buffer.)")

(defcustom erc-accidental-paste-threshold-seconds 1
  "Time in seconds that must pass between invocations of
  `erc-send-current-line' in order for that function to consider
  the new line to be intentional."
  :group 'erc
  :type '(choice number (other :tag "disabled" nil)))

(defun erc-send-current-line-with-paste-protection ()
  "Parse current line and send it to IRC, with accidental paste protection."
  (interactive)
  (let ((now (float-time)))
    (if (or (not erc-accidental-paste-threshold-seconds)
	    (< erc-accidental-paste-threshold-seconds (- now erc-last-input-time)))
        (save-restriction
	  (widen)
	  (if (< (point) (erc-beg-of-input-line))
	      (erc-error "Point is not in the input area")
	    (let ((inhibit-read-only t)
		  (str (erc-user-input))
		  (old-buf (current-buffer)))
	      (if (and (not (erc-server-buffer-live-p))
		       (not (erc-command-no-process-p str)))
		  (erc-error "ERC: No process running")
		(erc-set-active-buffer (current-buffer))

		;; Kill the input and the prompt
		(delete-region (erc-beg-of-input-line)
			       (erc-end-of-input-line))

		(unwind-protect
		    (erc-send-input str)
		  ;; Fix the buffer if the command didn't kill it
		  (when (buffer-live-p old-buf)
		    (with-current-buffer old-buf
		      (save-restriction
			(widen)
			(goto-char (point-max))
			(when (processp erc-server-process)
			  (set-marker (process-mark erc-server-process) (point)))
			(set-marker erc-insert-marker (point))
			(let ((buffer-modified (buffer-modified-p)))
			  (erc-display-prompt)
			  (set-buffer-modified-p buffer-modified))))))

		;; Only when last hook has been run...
		(run-hook-with-args 'erc-send-completed-hook str))))
	  (setq erc-last-input-time now))
      (switch-to-buffer "*ERC Accidental Paste Overflow*")
      (lwarn 'erc :warning "You seem to have accidentally pasted some text!"))))

(add-hook 'erc-mode-hook
          '(lambda ()
             (define-key erc-mode-map "\C-m" 'erc-send-current-line-with-paste-protection)
             ))
```

Note: The paste protection code is modified from a paste by user 'yashh' at
http://paste.lisp.org/display/78068 (Google cache
[here](http://webcache.googleusercontent.com/search?q=cache:p_S9ZKlWZPoJ:paste.lisp.org/display/78068+paste+78068&cd=1&hl=en&ct=clnk&gl=ca&source=www.google.ca)).
