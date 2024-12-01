import yake
import sys
import json

# def extract_keywords(text):
#     # Set up YAKE

#     keywords =
    
#     # Extract just the keywords (second item in each tuple)
#     keywords_list = [kw for kw, _ in keywords]
    
#     return keywords_list

if __name__ == "__main__":
    # Read the input text from the command line arguments
    text = sys.argv[1]
    kw_extractor = yake.KeywordExtractor()
    # Extract keywords
    keywords = kw_extractor.extract_keywords(text)
    
    # Output the keywords as a JSON array
    print(json.dumps(keywords))
