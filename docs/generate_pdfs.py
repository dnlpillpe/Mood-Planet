#!/usr/bin/env python3
"""
Genera los 3 PDF obligatorios de Mood Planet a partir de los Markdown en
docs/. Se ejecuta localmente o desde el workflow docs-build.yml.

Uso:
    python3 docs/generate_pdfs.py [directorio_salida]

Requiere: reportlab (pip install reportlab)
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.pagesizes import LETTER
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (
    SimpleDocTemplate,
    Paragraph,
    Spacer,
    ListFlowable,
    ListItem,
    Table,
    TableStyle,
    PageBreak,
)

DOCS_DIR = Path(__file__).resolve().parent
PURPLE = colors.HexColor("#5B4EE5")
PURPLE_DARK = colors.HexColor("#3A2E6E")

DOCUMENTS = [
    ("GUIA_PADRES_DOCENTES.md", "MoodPlanet_Guia_Padres_Docentes.pdf", "Mood Planet — Guía para madres, padres y docentes"),
    ("FICHA_TECNICA.md", "MoodPlanet_Ficha_Tecnica.pdf", "Mood Planet — Ficha técnica"),
    ("PRIVACIDAD.md", "MoodPlanet_Politica_Privacidad.pdf", "Mood Planet — Política de privacidad"),
]


def inline_markdown_to_html(text: str) -> str:
    """Convierte **negrita** y `código` inline a etiquetas soportadas por reportlab."""
    text = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", text)
    text = re.sub(r"`(.+?)`", r"<font face='Courier'>\1</font>", text)
    return text


def build_styles():
    styles = getSampleStyleSheet()
    styles.add(ParagraphStyle(name="MPTitle", fontSize=22, leading=26, textColor=PURPLE, spaceAfter=16, fontName="Helvetica-Bold"))
    styles.add(ParagraphStyle(name="MPH1", fontSize=16, leading=20, textColor=PURPLE_DARK, spaceBefore=14, spaceAfter=8, fontName="Helvetica-Bold"))
    styles.add(ParagraphStyle(name="MPH2", fontSize=13, leading=17, textColor=PURPLE_DARK, spaceBefore=10, spaceAfter=6, fontName="Helvetica-Bold"))
    styles.add(ParagraphStyle(name="MPBody", fontSize=10.5, leading=15, spaceAfter=8))
    styles.add(ParagraphStyle(name="MPQuote", fontSize=10.5, leading=15, spaceAfter=10, leftIndent=14, textColor=PURPLE_DARK, backColor=colors.HexColor("#F3F1FF"), borderPadding=8))
    return styles


def parse_table(lines: list[str]) -> list[list[str]]:
    rows = []
    for raw in lines:
        stripped = raw.strip()
        if not stripped.startswith("|"):
            continue
        row = [cell.strip() for cell in stripped.strip("|").split("|")]
        if re.match(r"^:?-+:?$", row[0]):  # saltar la línea separadora ---|---
            continue
        rows.append(row)
    return rows


BULLET_RE = re.compile(r"^(-|\*|\d+\.)\s+")


def _make_table(rows: list[list[str]], styles):
    header_style = ParagraphStyle(name="MPTableHeader", fontSize=9.5, leading=12, textColor=colors.white, fontName="Helvetica-Bold")
    cell_style = ParagraphStyle(name="MPTableCell", fontSize=9.5, leading=13)
    rendered_rows = []
    for row_index, row in enumerate(rows):
        style = header_style if row_index == 0 else cell_style
        rendered_rows.append([Paragraph(inline_markdown_to_html(cell), style) for cell in row])
    table = Table(rendered_rows, hAlign="LEFT")
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), PURPLE),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
        ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#CCCCCC")),
        ("FONTSIZE", (0, 0), (-1, -1), 9.5),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]))
    return table


def _split_bullet_items(lines: list[str]) -> list[str]:
    """Une líneas envueltas (soft-wrap) de una lista en un solo texto por ítem."""
    items: list[str] = []
    current = ""
    for raw in lines:
        stripped = raw.strip()
        if BULLET_RE.match(stripped):
            if current:
                items.append(current)
            current = BULLET_RE.sub("", stripped)
        else:
            current = f"{current} {stripped}".strip()
    if current:
        items.append(current)
    return items


def markdown_to_flowables(md_text: str, styles) -> list:
    flowables = []
    # Los bloques están separados por una o más líneas en blanco.
    blocks = re.split(r"\n\s*\n", md_text.strip())

    for block in blocks:
        lines = [l for l in block.split("\n") if l.strip()]
        if not lines:
            continue
        first = lines[0].strip()

        if first.startswith("| "):
            rows = parse_table(lines)
            if rows:
                flowables.append(_make_table(rows, styles))
                flowables.append(Spacer(1, 10))
            continue

        if first.startswith("### "):
            flowables.append(Paragraph(inline_markdown_to_html(first[4:]), styles["MPH2"]))
            continue
        if first.startswith("## "):
            flowables.append(Paragraph(inline_markdown_to_html(first[3:]), styles["MPH1"]))
            continue
        if first.startswith("# "):
            flowables.append(Paragraph(inline_markdown_to_html(first[2:]), styles["MPTitle"]))
            continue

        if first.startswith("> "):
            joined = " ".join(l.strip()[2:].strip() if l.strip().startswith("> ") else l.strip() for l in lines)
            flowables.append(Paragraph(inline_markdown_to_html(joined), styles["MPQuote"]))
            continue

        if BULLET_RE.match(first):
            items = _split_bullet_items(lines)
            list_items = [ListItem(Paragraph(inline_markdown_to_html(item), styles["MPBody"])) for item in items]
            flowables.append(ListFlowable(list_items, bulletType="bullet", start="•", leftIndent=16))
            continue

        # Párrafo normal: se une todo el bloque en una sola línea.
        joined = " ".join(l.strip() for l in lines)
        flowables.append(Paragraph(inline_markdown_to_html(joined), styles["MPBody"]))

    return flowables


def generate_pdf(markdown_path: Path, output_path: Path, title: str, styles) -> None:
    text = markdown_path.read_text(encoding="utf-8")
    doc = SimpleDocTemplate(
        str(output_path),
        pagesize=LETTER,
        topMargin=2.2 * cm,
        bottomMargin=2 * cm,
        leftMargin=2 * cm,
        rightMargin=2 * cm,
        title=title,
    )
    flowables = markdown_to_flowables(text, styles)
    doc.build(flowables)
    print(f"  -> {output_path.name}")


def main() -> None:
    out_dir = Path(sys.argv[1]) if len(sys.argv) > 1 else DOCS_DIR / "pdf"
    out_dir.mkdir(parents=True, exist_ok=True)
    styles = build_styles()

    print("Generando PDFs de Mood Planet...")
    for source_name, output_name, title in DOCUMENTS:
        source_path = DOCS_DIR / source_name
        generate_pdf(source_path, out_dir / output_name, title, styles)
    print(f"Listo. {len(DOCUMENTS)} PDFs generados en {out_dir}")


if __name__ == "__main__":
    main()
