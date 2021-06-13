// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

(async function() {
  TestRunner.addResult(`Tests inspector ParsedURL class\n`);

  parseAndDumpURL('http://example.com/?queryParam1=value1&queryParam2=value2#fragmentWith/Many//Slashes');
  parseAndDumpURL('http://example.com/foo.html?queryParam1=value1@&queryParam2=value2#fragmentWith/Many//Slashes');
  parseAndDumpURL(
      'http://user42:Alina-!$&@example.com/foo/bar.html?queryParam1=value1&queryParam2=value2#fragmentWith/Many//Slashes');
  parseAndDumpURL(
      'http://foo@example.com/foo/bar/baz.html?queryParam1=value1&queryParam2=value2#fragmentWith/Many//Slashes');
  parseAndDumpURL('://');
  parseAndDumpURL('');

  // support IPv6 localhost
  parseAndDumpURL('http://[::]/?queryParam1=value1&queryParam2=value2#fragmentWith/Many//Slashes');

  // Test how double (and more than double) slashes are parsed.
  parseAndDumpURL('http://example.com//?queryParam1=value1');
  parseAndDumpURL('http://example.com//foo.html');
  parseAndDumpURL('http://example.com//foo/bar.html');
  parseAndDumpURL('http://example.com/foo//bar.html');
  parseAndDumpURL('http://example.com//foo//bar.html');
  parseAndDumpURL('http://example.com//foo/bar/baz.html');
  parseAndDumpURL('http://example.com/foo//bar/baz.html');
  parseAndDumpURL('http://example.com/foo/bar//baz.html');
  parseAndDumpURL('http://example.com///foo/bar/baz.html');
  parseAndDumpURL('http://example.com/foo////bar/baz.html');
  parseAndDumpURL('http://example.com/foo/bar/////baz.html');

  testSplitLineColumn('http://www.monyhar.org');
  testSplitLineColumn('http://www.monyhar.org:8000');
  testSplitLineColumn('http://www.monyhar.org:8000/');
  testSplitLineColumn('http://www.monyhar.org:8000/foo.js:10');
  testSplitLineColumn('http://www.monyhar.org:8000/foo.js:10:20');
  testSplitLineColumn('http://www.monyhar.org/foo.js:10');
  testSplitLineColumn('http://www.monyhar.org/foo.js:10:20');

  testExtractExtension('http://example.com/foo.html');
  testExtractExtension('http://example.com/foo.html?hello');
  testExtractExtension('http://example.com/foo.html?#hello');
  testExtractExtension('http://example.com/foo.ht#ml?hello');
  testExtractExtension('http://example.com/foo.ht?ml#hello');
  testExtractExtension('http://example.com/fooht?ml#hello');
  testExtractExtension('/some/folder/');
  testExtractExtension('/some/folder/file.js');
  testExtractExtension('/some/folder/file');
  testExtractExtension('/some/folder/folder.js/file');
  testExtractExtension('/some/folder/folder.js/file.png');
  testExtractExtension('/some/folder/folder.js/');
  testExtractExtension('/some/folder/');

  testExtractExtension('http://example.com/foo.html%20hello');
  testExtractExtension('http://example.com/foo.html%20?#hello');
  testExtractExtension('http://example.com/foo.html?%20#hello');
  testExtractExtension('http://example.com/foo.html?#%20hello');
  testExtractExtension('http://example.com/foo.ht%20');
  testExtractExtension('http://example.com/foo.ht?ml#hello%20');
  testExtractExtension('/some/folder/folder.js%20/');

  testURLWithoutHash('http://example.com/#hello');
  testURLWithoutHash('http://example.com/#?hello');
  testURLWithoutHash('http://example.com/?#hello');
  testURLWithoutHash('http://example.com/?hello#hello');
  testURLWithoutHash('http://example.com/hello#?hello#hello');

  TestRunner.completeTest();

  function testURLWithoutHash(url) {
    TestRunner.addResult('URL: ' + url);
    TestRunner.addResult('Without Hash: ' + Common.ParsedURL.urlWithoutHash(url));
    TestRunner.addResult('');
  }

  /**
   * @param {string} url
   */
  function testExtractExtension(url) {
    TestRunner.addResult('URL: ' + url);
    TestRunner.addResult('Extension: ' + Common.ParsedURL.extractExtension(url));
    TestRunner.addResult('');
  }

  /**
   * @param {string} url
   */
  function testSplitLineColumn(url) {
    var result = Common.ParsedURL.splitLineAndColumn(url);

    TestRunner.addResult('Splitting url: ' + url);
    TestRunner.addResult('  URL: ' + result.url);
    TestRunner.addResult('  Line: ' + result.lineNumber);
    TestRunner.addResult('  Column: ' + result.columnNumber);
  }

  /**
   * @param {string} url
   */
  function parseAndDumpURL(url) {
    var parsedURL = new Common.ParsedURL(url);

    TestRunner.addResult('Parsing url: ' + url);
    TestRunner.addResult('  isValid: ' + parsedURL.isValid);
    TestRunner.addResult('  scheme: ' + parsedURL.scheme);
    TestRunner.addResult('  user: ' + parsedURL.user);
    TestRunner.addResult('  host: ' + parsedURL.host);
    TestRunner.addResult('  port: ' + parsedURL.port);
    TestRunner.addResult('  path: ' + parsedURL.path);
    TestRunner.addResult('  queryParams: ' + parsedURL.queryParams);
    TestRunner.addResult('  fragment: ' + parsedURL.fragment);
    TestRunner.addResult('  folderPathComponents: ' + parsedURL.folderPathComponents);
    TestRunner.addResult('  lastPathComponent: ' + parsedURL.lastPathComponent);
  }
})();
