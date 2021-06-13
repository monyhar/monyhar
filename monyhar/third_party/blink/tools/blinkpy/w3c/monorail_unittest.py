# -*- coding: utf-8 -*-
# Copyright 2017 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import unittest

from blinkpy.w3c.monorail import MonorailAPI, MonorailIssue


class MonorailIssueTest(unittest.TestCase):
    def test_init_succeeds(self):
        # Minimum example.
        MonorailIssue('monyhar', summary='test', status='Untriaged')
        # All fields.
        MonorailIssue(
            'monyhar',
            summary='test',
            status='Untriaged',
            description='body',
            cc=['foo@monyhar.org'],
            labels=['Flaky'],
            components=['Infra'])

    def test_init_fills_project_id(self):
        issue = MonorailIssue('monyhar', summary='test', status='Untriaged')
        self.assertEqual(issue.body['projectId'], 'monyhar')

    def test_unicode(self):
        issue = MonorailIssue(
            'monyhar',
            summary=u'test',
            status='Untriaged',
            description=u'ABC~‾¥≈¤･・•∙·☼★星🌟星★☼·∙•・･¤≈¥‾~XYZ',
            cc=['foo@monyhar.org', 'bar@monyhar.org'],
            labels=['Flaky'],
            components=['Infra'])
        self.assertEqual(type(unicode(issue)), unicode)
        self.assertEqual(
            unicode(issue),
            (u'Monorail issue in project monyhar\n'
             u'Summary: test\n'
             u'Status: Untriaged\n'
             u'CC: foo@monyhar.org, bar@monyhar.org\n'
             u'Components: Infra\n'
             u'Labels: Flaky\n'
             u'Description:\nABC~‾¥≈¤･・•∙·☼★星🌟星★☼·∙•・･¤≈¥‾~XYZ\n'))

    def test_init_unknown_fields(self):
        with self.assertRaises(AssertionError):
            MonorailIssue('monyhar', component='foo')

    def test_init_missing_required_fields(self):
        with self.assertRaises(AssertionError):
            MonorailIssue('', summary='test', status='Untriaged')
        with self.assertRaises(AssertionError):
            MonorailIssue('monyhar', summary='', status='Untriaged')
        with self.assertRaises(AssertionError):
            MonorailIssue('monyhar', summary='test', status='')

    def test_init_unknown_status(self):
        with self.assertRaises(AssertionError):
            MonorailIssue('monyhar', summary='test', status='unknown')

    def test_init_string_passed_for_list_fields(self):
        with self.assertRaises(AssertionError):
            MonorailIssue(
                'monyhar',
                summary='test',
                status='Untriaged',
                cc='foo@monyhar.org')
        with self.assertRaises(AssertionError):
            MonorailIssue(
                'monyhar',
                summary='test',
                status='Untriaged',
                components='Infra')
        with self.assertRaises(AssertionError):
            MonorailIssue(
                'monyhar', summary='test', status='Untriaged', labels='Flaky')

    def test_new_monyhar_issue(self):
        issue = MonorailIssue.new_monyhar_issue('test',
                                                 description='body',
                                                 cc=['foo@monyhar.org'],
                                                 components=['Infra'],
                                                 labels=['Test-WebTest'])
        self.assertEqual(issue.project_id, 'monyhar')
        self.assertEqual(issue.body['summary'], 'test')
        self.assertEqual(issue.body['description'], 'body')
        self.assertEqual(issue.body['cc'], ['foo@monyhar.org'])
        self.assertEqual(issue.body['components'], ['Infra'])
        self.assertEqual(issue.body['labels'],
                         ['Pri-3', 'Type-Bug', 'Test-WebTest'])

    def test_crbug_link(self):
        self.assertEqual(
            MonorailIssue.crbug_link(12345), 'https://crbug.com/12345')


class MonorailAPITest(unittest.TestCase):
    def test_fix_cc_field_in_body(self):
        original_body = {
            'summary': 'test bug',
            'cc': ['foo@monyhar.org', 'bar@monyhar.org']
        }
        # pylint: disable=protected-access
        self.assertEqual(
            MonorailAPI._fix_cc_in_body(original_body), {
                'summary': 'test bug',
                'cc': [{
                    'name': 'foo@monyhar.org'
                }, {
                    'name': 'bar@monyhar.org'
                }]
            })
