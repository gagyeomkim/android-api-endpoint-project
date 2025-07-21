# @author: gagyeomkim
# @since: 2025-07-21
import os
import re
import argparse
from collections import defaultdict
from rich.console import Console
from rich.table import Table
from rich.panel import Panel
from rich.text import Text
from pathlib import Path

# 난독화된 annotation을 실제 HTTP 메소드로 변환하기 위함
ANNOTATION_MAP = {
    "f": "GET",
    "o": "POST",
    "b": "DELETE",
    "p": "PUT"
}

# path 탐색을 위한 정규표현식
ENDPOINT_PATTERN = re.compile(
    r"\.annotation runtime "
    r"(?:L\S+/)?([a-z]+);?" # 1번 group: annotation 이름인 소문자를 캡쳐
    r"\s*?"
    r'value = "(.+?)"', # 2번 group: value 부분에 있는 path 캡쳐
    re.DOTALL
)

# 상세 path를 추출하는 함수
def extract_endpoints_path(folder_path):
    print(f"[*] target folder: {Path(folder_path).name}\nAPI path 후보 탐색을 시작합니다...")
    found_path = defaultdict(list) # (http method, path): [파일위치....] 구조
    unknown_tokens = defaultdict(list) # (unk_annotation, path): [파일위치...] 구조
    
    # ref: https://toramko.tistory.com/entry/python-oslistdir%EA%B3%BC-oswalk-%ED%8C%8C%EC%9D%B4%EC%8D%AC-%ED%8A%B9%EC%A0%95-%EA%B2%BD%EB%A1%9C-%EB%82%B4-%EB%94%94%EB%A0%89%ED%86%A0%EB%A6%AC%EC%99%80-%ED%8C%8C%EC%9D%BC-%EA%B2%80%EC%83%89
    for root, _, files in os.walk(folder_path):
        for file in files:
            if file.endswith(".smali"): # 확장자 체크
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        content = f.read()

                    for match in ENDPOINT_PATTERN.finditer(content):
                        annotation = match.group(1)
                        path = match.group(2)
                        # ANNOTATION_MAP에 등록되지 않은 annotation 처리
                        if annotation not in ANNOTATION_MAP:
                            unk_key = (annotation, path)
                            if file not in unknown_tokens[unk_key]:
                                unknown_tokens[unk_key].append(file)
                            continue
                        
                        # ANNOTATION_MAP에 등록된 annotation 처리
                        http_method = ANNOTATION_MAP.get(annotation.lower()) 
                        endpoint_key = (http_method, path)  # found_path의 key
                        
                        # 처음 발견된 file이라면, path가 발견된 파일들을 저장하기 위한 리스트 생성
                        if file not in found_path[endpoint_key]:
                             found_path[endpoint_key].append(file)  # 파일위치가 없다면 추가
                             
                except Exception as e:
                    print(f"[!] '{file_path}' 파일 처리 중 오류 발생: {e}")
    
    return found_path, unknown_tokens

# 출력 결과를 꾸미는 코드: GEMINI로 작성
def print_results(found_path, unknown_tokens):
    """추출된 결과를 Rich 라이브러리를 사용해 터미널에 출력."""
    console = Console()

    # 1. 탐색 결과 테이블 출력
    if found_path:
        console.print("\n[bold cyan]✅ 발견된 API path[/bold cyan]")
        table = Table(header_style="bold magenta", border_style="dim")
        table.add_column("HTTP Method", style="cyan", width=12)
        table.add_column("Endpoint Path", style="green")
        table.add_column("발견된 파일", style="yellow")

        sorted_endpoints = sorted(found_path.items(), key=lambda item: item[0][1])
        
        for (method, path), files_found in sorted_endpoints:
            files_str = "\n".join(sorted(list(set(files_found))))
            table.add_row(method, path, files_str)

        console.print(table)
    else:
        console.print(Panel.fit("[bold red]발견된 path가 없습니다.[/bold red]", title="탐색 결과"))

    # 2. 요약 정보 출력
    summary_text = Text.assemble(
        ("총 ", "bold"),
        (str(len(found_path)), "bold yellow"),
        (" 개의 path를 찾았습니다.", "bold")
    )
    console.print(Panel(summary_text, title="[dim]Summary[/dim]"))

    # 3. 알 수 없는 토큰 목록 출력
    if unknown_tokens:
        # ANNOTATION_MAP에 추가하라는 안내 메시지
        console.print("\n[yellow]⚠️ 알 수 없는 어노테이션 토큰[/yellow]")
        panel_content = Text("아래의 토큰들을 ANNOTATION_MAP에 추가하는 것을 고려해 보세요.", style="default")
        console.print(Panel.fit(panel_content, border_style="yellow"))
        # 알 수 없는 토큰을 위한 테이블 생성
        unknown_table = Table(header_style="bold yellow", border_style="dim")
        unknown_table.add_column("Unknown Token", style="red")
        unknown_table.add_column("Endpoint Path", style="green")
        unknown_table.add_column("발견된 파일", style="yellow")

        # 경로 기준으로 정렬
        sorted_unknowns = sorted(unknown_tokens.items(), key=lambda item: item[0][1])

        for (token, path), files_found in sorted_unknowns:
            files_str = "\n".join(sorted(list(set(files_found))))
            unknown_table.add_row(token, path, files_str)
            
        console.print(unknown_table)
        # 2. 요약 정보 출력
        # 고유한 토큰 이름만 추출하여 개수를 셈
        unknown_tokens_token = set(token for token, _ in unknown_tokens.keys())
        unknown_tokens_path = set(path for _ , path in unknown_tokens.keys())

        summary_text = Text.assemble(
            ("총 ", "bold"),
            (str(len(unknown_tokens_token)), "bold yellow"), # <--- 이 부분을 수정
            (" 종류의 알 수 없는 annotation을 발견했으며, 분석해야할 ", "bold"),
            (str(len(unknown_tokens_path)), "bold yellow"), # <--- 이 부분을 수정
            ("개의 추가적인 path가 존재합니다.", "bold")
        )
        console.print(Panel(summary_text, title="[dim]Summary[/dim]"))

def main():
    parser = argparse.ArgumentParser(description='Smali 폴더에서 Retrofit API path를 추출하고 결과를 출력합니다.')
    parser.add_argument("folder_path", help="분석할 Smali 폴더 경로")
    args = parser.parse_args()  # 사용자 입력을 파싱,     
                                # 예를 들어 위에서 ./smali를 입력했다면 args.folder_path == "./smali"

    # path 추출
    path, tokens = extract_endpoints_path(args.folder_path)
    print_results(path, tokens)


if __name__ == "__main__":
    main()