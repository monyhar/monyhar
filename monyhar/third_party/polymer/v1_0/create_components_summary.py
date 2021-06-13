# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import json
import os
import re


COMPONENTS_DIR = 'components'
DESTINATION_COMPONENTS_DIR = 'components-monyhar'
COMPONENT_SUMMARY =\
"""Name: %(name)s
Repository: %(repository)s
Tree: %(tree)s
Revision: %(revision)s
Tree link: %(tree_link)s
"""


def PrintSummary(info):
  repository = info['_source']
  resolution = info['_resolution']
  tree = GetTreeishName(resolution)
  # Convert to web link.
  repository_web = re.sub('^git:', 'https:', re.sub('\.git$', '',  repository))
  # Specify tree to browse to.
  tree_link = repository_web + '/tree/' + tree
  print COMPONENT_SUMMARY % {
    'name': info['name'],
    'repository': repository,
    'tree': tree,
    'revision': resolution['commit'],
    'tree_link': tree_link
  }


def GetTreeishName(resolution):
  """Gets the name of the tree-ish (branch, tag or commit)."""
  if resolution['type'] == 'branch':
    return resolution['branch']
  if resolution['type'] in ('version', 'tag'):
    return resolution['tag']
  return resolution['commit']


def main():
  for entry in sorted(os.listdir(DESTINATION_COMPONENTS_DIR)):
    component_path = os.path.join(COMPONENTS_DIR, entry)
    if not os.path.isdir(component_path):
      continue
    bower_path = os.path.join(component_path, '.bower.json')
    if not os.path.isfile(bower_path):
      raise Exception('%s is not a file.' % bower_path)
    with open(bower_path) as stream:
      info = json.load(stream)
    PrintSummary(info)


if __name__ == '__main__':
  main()
