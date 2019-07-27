# diff-format
This library performs a diff operation (google: diff_match_patch) and preserves text formatting (Bold etc)

# Diff Logic #
The diffing library used here is the google: diff_match_patch (https://github.com/google/diff-match-patch) with following changes:
  * Removed the string length restriction on lineMode diff ( Commit: [4f4bebf](https://github.com/ojuskul/diff-format/commit/4f4bebfda648d0d7c431a53a242c28f9e964f2f6))

