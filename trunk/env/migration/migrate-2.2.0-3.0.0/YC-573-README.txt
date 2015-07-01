REF: YC-573 noimage directory is lowercase by default which causes issues on unix systems

To prevent system discrepancies all intermediate directories are now upper case only.
For example
Path with upper case code: someproduct_CODE_a.jpg would resolve to C/CODE/someproduct_CODE_a.jpg
Path with lower case code: someproduct_code_a.jpg would resolve to C/code/someproduct_code_a.jpg

Actions for production systems:
Copy all image files that have lowercase intermediate directories to upper case directoru names.
I.e. c/code/someproduct_code_a.jpg should become C/code/someproduct_code_a.jpg on the file system