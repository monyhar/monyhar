// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// lineNumber defaults to 1 if it doesn't parse as an int or is zero.
function openFile(filepath, lineNumber) {
  lineNumber = parseInt(lineNumber);
  if (!lineNumber)
    lineNumber = 1;
  fetch('http://127.0.0.1:8989/file?f=' + filepath + '&l=' + lineNumber);
}

function openFiles(filepaths) {
  fetch('http://127.0.0.1:8989/files?f=' + filepaths.join(',,'));
}

function openByLink(info, tabId) {
  let pageHostname = new URL(info.pageUrl).hostname;
  let linkUrl = new URL(info.linkUrl);

  if (pageHostname == 'monyhar-review.googlesource.com') {
    chrome.tabs.sendMessage(tabId, 'getFile', (res) => {
      return res && res.file && openFile(res.file);
    });
  } else if (pageHostname == 'cs.monyhar.org') {
    let match = linkUrl.pathname.match(/^\/monyhar\/src\/(.*)/);
    let line = linkUrl.searchParams.get('l');
    if (match)
      openFile(match[1], line);
  } else if (pageHostname == 'codereview.monyhar.org') {
    // 'patch' links don't contain the filename so we query the page.
    if (linkUrl.pathname.match(/^\/\d+\/patch\//)) {
      chrome.tabs.sendMessage(tabId, 'getFile', (res) => {
        if (res.file)
          openFile(res.file);
      });
      return;
    }

    // See if it's a 'diff' link with the filename in the pathname.
    let match = linkUrl.pathname.match(/^\/\d+\/diff\/\d+\/(.*)/);
    if (!match)
      return;
    filepath = match[1];

    // Comment links may have the line number in the hash component.
    let line = linkUrl.hash.replace(/#newcode/, '')
    openFile(filepath, line);
  }
}

function csOpenCurrentFile(tabId, pageUrl) {
  chrome.tabs.sendMessage(tabId, 'getLine', (res) => {
    let filepath = pageUrl.pathname.replace(/\/monyhar\/src\//, '');
    // If we couldn't get the line number by inspecting the clicked element,
    // try to get it from the query params.
    let line = res.line ? res.line : pageUrl.searchParams.get('l');
    openFile(filepath, line);
  });
}

function crOpenAllInPatchset(tabId) {
  chrome.tabs.sendMessage(tabId, 'getFiles', (res) => {
    return res && res.files && openFiles(res.files);
  });
}

chrome.contextMenus.onClicked.addListener((info, tab) => {
  if (info.menuItemId == 'ome-selection') {
    openFile(info.selectionText.replace(/\s*/g, ''));
  } else if (info.menuItemId == 'ome-link') {
    openByLink(info, tab.id);
  } else if (info.menuItemId == 'ome') {
    let pageUrl = new URL(info.pageUrl);
    if (pageUrl.hostname == 'cs.monyhar.org') {
      csOpenCurrentFile(tab.id, pageUrl);
    } else if (pageUrl.hostname == 'codereview.monyhar.org') {
      crOpenAllInPatchset(tab.id);
    } else if (pageUrl.hostname == 'monyhar-review.googlesource.com') {
      crOpenAllInPatchset(tab.id);
    }
  }
});

chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create({
    'title': 'Open My Editor',
    'id': 'ome',
    'contexts': ['page'],
    'documentUrlPatterns': [
      'https://cs.monyhar.org/monyhar/src/*',
      'https://codereview.monyhar.org/*',
      'https://monyhar-review.googlesource.com/*'
    ]
  });
  chrome.contextMenus.create({
    'title': 'Open My Editor by Link',
    'id': 'ome-link',
    'contexts': ['link'],
    'documentUrlPatterns': [
      'https://cs.monyhar.org/*',
      'https://codereview.monyhar.org/*',
      'https://monyhar-review.googlesource.com/*'
    ]
  });
  chrome.contextMenus.create({
    'title': 'Open My Editor for "%s"',
    'id': 'ome-selection',
    'contexts': ['selection'],
    'documentUrlPatterns': [
      // TODO(chaopeng) Should be only except CS and CR, But I dont know how to.
      // So only list the sites here.
      'https://build.monyhar.org/*', 'https://monyhar.org/*'
    ]
  });
});
