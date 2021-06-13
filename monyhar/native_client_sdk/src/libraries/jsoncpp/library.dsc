{
  'SEARCH': [
    '.',
    '../../../../third_party/jsoncpp/source/include/json',
    '../../../../third_party/jsoncpp/source/src/lib_json',
    '../../../../third_party/jsoncpp',
  ],
  'TARGETS': [
    {
      'NAME' : 'jsoncpp',
      'TYPE' : 'lib',
      'DEFINES': ['JSON_USE_EXCEPTION=0'],
      'SOURCES' : [
        'json_reader_fix.cpp',
        'json_value.cpp',
        'json_writer.cpp',
      ],
      'CXXFLAGS': ['-Wno-strict-aliasing']
    }
  ],
  'HEADERS': [
    {
      'DEST': 'include/json',
      'FILES': [
        'assertions.h',
        'autolink.h',
        'config.h',
        'features.h',
        'forwards.h',
        'json.h',
        'reader.h',
        'value.h',
        'writer.h',
      ],
    },
  ],
  'DATA': [
    'LICENSE',
    'README.monyhar',
    'json_batchallocator.h',
    'json_internalarray.inl',
    'json_internalmap.inl',
    'json_tool.h',
    'json_valueiterator.inl',
    'json_reader.cpp',
  ],
  'DEST': 'src',
  'NAME': 'jsoncpp',
}
