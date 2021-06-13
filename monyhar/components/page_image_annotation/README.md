# //components/page_image_annotation

Library for using the image annotation service on images on webpages.

The image annotation service performs general image processing / labeling tasks
in Chromium. This library enables use of the service with webpages; for example,
by tracking images on webpages and sending their pixel data to the service.

This is a layered component
(https://sites.google.com/a/monyhar.org/dev/developers/design-documents/layered-components-design)
which allows it to be shared cleanly on iOS.
