import json

def find_missing_questions(pattern_file, questions_file):
    # Load the files
    with open(pattern_file, 'r') as pf, open(questions_file, 'r') as qf:
        patterns_data = json.load(pf)
        questions_data = json.load(qf)

    # Extract all tagNames from pattern.json
    all_tag_names = set()
    for pattern in patterns_data['patterns']:
        for tag in pattern['tags']:
            all_tag_names.add(tag['tagName'])

    # Extract existing tagNames from questions.json
    existing_questions = {q['tagName'] for q in questions_data['questions']}

    # Find missing tagNames
    missing_tag_names = all_tag_names - existing_questions

    return missing_tag_names

# File paths
pattern_file = 'pattern.json'  # Replace with the actual file path
questions_file = 'questions.json'  # Replace with the actual file path

# Find and print missing tagNames
missing_tags = find_missing_questions(pattern_file, questions_file)
print("Missing tagNames without questions:")
print(missing_tags)